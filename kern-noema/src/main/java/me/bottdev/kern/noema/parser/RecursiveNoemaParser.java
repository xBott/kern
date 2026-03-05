package me.bottdev.kern.noema.parser;

import lombok.RequiredArgsConstructor;
import me.bottdev.kern.noema.ast.*;
import me.bottdev.kern.noema.ast.nodes.KeyValueNode;
import me.bottdev.kern.noema.ast.nodes.LeafValueNode;
import me.bottdev.kern.noema.ast.nodes.ListNode;
import me.bottdev.kern.noema.ast.nodes.SectionNode;
import me.bottdev.kern.noema.result.NoemaError;
import me.bottdev.kern.noema.result.NoemaResult;
import me.bottdev.kern.noema.result.NoemaResultBuilder;
import me.bottdev.kern.noema.token.Token;
import me.bottdev.kern.noema.token.TokenStream;
import me.bottdev.kern.noema.token.TokenType;

import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class RecursiveNoemaParser implements NoemaParser {

    private final TokenStream tokenStream;

    @Override
    public NoemaResult<NoemaSyntaxTree> parse() {

        NoemaResultBuilder<NoemaSyntaxTree> resultBuilder = new NoemaResultBuilder<>();
        NoemaSyntaxTree.NoemaSyntaxTreeBuilder treeBuilder = NoemaSyntaxTree.builder();

        Token token = tokenStream.lookahead(0);

        while (token != null) {
            parseElement().ifSuccessOrElse(
                    treeBuilder::root,
                    resultBuilder::responses
            );
            token = tokenStream.lookahead(0);
        }

        NoemaSyntaxTree syntaxTree = treeBuilder.build();
        resultBuilder.value(syntaxTree);

        return resultBuilder.build();
    }

    public NoemaResult<NoemaNode> parseElement() {

        Token token = tokenStream.consume();

        if (token == null) {
            return null;
        }

        return switch (token.tokenType()) {
            case KEY -> parseKeyValue(token);
            case VALUE -> parseValue(token);
            case SECTION_START -> parseSection(token);
            case LIST_START -> parseList(token);
            case SECTION_END -> new NoemaResultBuilder<NoemaNode>()
                    .response(
                            NoemaError.parse(
                                    "Unexpected SECTION_END",
                                    token
                            )
                    )
                    .build();
            case LIST_END -> new NoemaResultBuilder<NoemaNode>()
                    .response(
                            NoemaError.parse(
                                    "Unexpected LIST_END",
                                    token
                            )
                    )
                    .build();
        };
    }


    private NoemaResult<NoemaNode> parseKeyValue(Token startToken) {

        NoemaResultBuilder<NoemaNode> resultBuilder = new NoemaResultBuilder<>();

        Token nextToken = tokenStream.consume();

        if (nextToken == null) {
            return resultBuilder
                    .response(
                            NoemaError.parse(
                                    "Unexpected end of tokens after KEY",
                                    startToken
                            )
                    )
                    .build();
        }

        String key = startToken.lexeme();

        NoemaResult<NoemaNode> valueNodeResult = switch (nextToken.tokenType()) {
            case VALUE -> parseValue(nextToken);
            case SECTION_START -> parseSection(startToken);
            case LIST_START -> parseList(startToken);
            default -> new NoemaResultBuilder<NoemaNode>()
                    .response(
                            NoemaError.parse(
                                    "Unexpected token '" + nextToken.tokenType() + "'",
                                    nextToken
                            )
                    )
                    .build();
        };

        valueNodeResult.ifSuccessOrElse(
                valueNode -> resultBuilder.value(
                        KeyValueNode.builder()
                                .key(key)
                                .valueNode(valueNode)
                                .build()
                ),
                resultBuilder::responses
        );

        return resultBuilder.build();
    }


    private NoemaResult<NoemaNode> parseValue(Token startToken) {

        LeafValueNode node = LeafValueNode.builder()
                .token(startToken)
                .value(startToken.lexeme())
                .build();

        return new NoemaResultBuilder<NoemaNode>()
                .value(node)
                .build() ;
    }


    private NoemaResult<NoemaNode> parseSection(Token startToken) {

        NoemaResultBuilder<NoemaNode> resultBuilder = new NoemaResultBuilder<>();

        SectionNode.SectionNodeBuilder nodeBuilder = SectionNode.builder()
                .token(startToken);

        Token next = tokenStream.lookahead(0);

        while (next != null && next.tokenType() != TokenType.SECTION_END) {

            parseElement().ifSuccessOrElse(
                    nodeBuilder::child,
                    resultBuilder::responses
            );

            next = tokenStream.lookahead(0);
        }

        tokenStream.consume();

        return resultBuilder
                .value(nodeBuilder.build())
                .build();
    }

    private NoemaResult<NoemaNode> parseList(Token startToken) {

        NoemaResultBuilder<NoemaNode> resultBuilder = new NoemaResultBuilder<>();

        ListNode.ListNodeBuilder nodeBuilder = ListNode.builder()
                .token(startToken);

        Token next = tokenStream.lookahead(0);

        AtomicInteger index = new AtomicInteger();
        while (next != null && next.tokenType() != TokenType.LIST_END) {

            parseElement().ifSuccessOrElse(
                    nodeBuilder::child,
                    resultBuilder::responses
            );

            next = tokenStream.lookahead(0);
        }

        tokenStream.consume();

        return resultBuilder
                .value(nodeBuilder.build())
                .build();
    }


}
