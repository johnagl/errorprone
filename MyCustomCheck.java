/*
 * Copyright 2016 The Error Prone Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.errorprone.sample;

import static com.google.common.collect.Iterables.getLast;
import static com.google.errorprone.BugPattern.LinkType.CUSTOM;
import static com.google.errorprone.BugPattern.SeverityLevel.ERROR;
import static com.google.errorprone.matchers.Description.NO_MATCH;
import static com.google.errorprone.matchers.Matchers.instanceMethod;
import static com.google.errorprone.matchers.method.MethodMatchers.staticMethod;
import static com.google.errorprone.matchers.Matchers.constructor;

import com.google.auto.service.AutoService;
import com.google.common.collect.Iterables;
import com.google.errorprone.BugPattern;
import com.google.errorprone.VisitorState;
import com.google.errorprone.bugpatterns.BugChecker;
import com.google.errorprone.bugpatterns.BugChecker.ClassTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.MethodTreeMatcher;
import com.google.errorprone.bugpatterns.BugChecker.NewClassTreeMatcher;
import com.google.errorprone.bugpatterns.MisusedWeekYear;
import com.google.errorprone.bugpatterns.BugChecker.MethodInvocationTreeMatcher;
import com.google.errorprone.fixes.SuggestedFix;
import com.google.errorprone.matchers.Description;
import com.google.errorprone.matchers.Matcher;
import com.google.errorprone.matchers.Suppressible;
import com.google.errorprone.util.ASTHelpers;
import com.sun.source.tree.*;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.code.Symbol;
import com.sun.tools.javac.tree.JCTree;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.text.DateFormat;

/** Matches on string formatting inside print methods. */
@AutoService(BugChecker.class)
@BugPattern(
        name = "MyCustomCheck",
        summary = "Date formatting",
        severity = ERROR,
        linkType = CUSTOM,
        link = "example.com/bugpattern/MyCustomCheck")
public class MyCustomCheck extends BugChecker implements NewClassTreeMatcher {

    private static final String JAVA_SIMPLE_DATE_FORMAT = "java.text.SimpleDateFormat";

    Matcher<ExpressionTree> SIMPLE_DATE =
            constructor().forClass(JAVA_SIMPLE_DATE_FORMAT).withParameters("java.lang.String");
    

    @Override
    public Description matchNewClass(NewClassTree tree, VisitorState state) {
        if (!SIMPLE_DATE.matches(tree, state)) {
            return NO_MATCH;
        }
        if (SIMPLE_DATE.matches(tree, state)) {
            String pattern = tree.getArguments().toString();
            System.out.println("PATTERN: " + pattern);
            if (pattern.contains("YYYY") && !pattern.contains("w")) {
                return describeMatch(tree);
            } else {
                return NO_MATCH;
            }

        }
        return describeMatch(tree);
    }
}