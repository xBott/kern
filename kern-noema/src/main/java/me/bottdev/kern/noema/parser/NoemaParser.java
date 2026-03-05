package me.bottdev.kern.noema.parser;

import me.bottdev.kern.noema.ast.NoemaSyntaxTree;
import me.bottdev.kern.noema.result.NoemaResult;

public interface NoemaParser {

    NoemaResult<NoemaSyntaxTree> parse();

}
