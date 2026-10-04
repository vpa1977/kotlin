/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.codegen;

import kotlin.Pair;

import java.util.Collection;

/**
 * Stub for the former D8/R8 dex checker.
 *
 * The dependency on {@code com.android.tools:r8} was removed, but the public API is kept
 * so that existing callers continue to compile.
 */
public class D8Checker {

    public static final boolean RUN_D8_CHECKER = false;

    private D8Checker() {
    }

    public static void check(ClassFileFactory outputFiles) {
    }

    public static void checkFilesWithD8(Collection<Pair<byte[], String>> classFiles) {
    }
}
