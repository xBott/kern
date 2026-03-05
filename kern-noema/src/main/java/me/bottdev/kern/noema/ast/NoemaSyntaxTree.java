package me.bottdev.kern.noema.ast;

import lombok.*;

import java.util.List;

@Builder
public record NoemaSyntaxTree(@Singular List<NoemaNode> roots) {}
