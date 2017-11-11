/*
 * Copyright (c) 2017 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.enadim.spring.cloud.ribbon.propagator;

import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Includes/Excludes pattern filter.
 */
@Getter
public class PatternFilter implements Filter<String> {
    /**
     * include patterns
     */
    private final List<Pattern> includes;
    /**
     * exclude patterns
     */
    private final List<Pattern> excludes;

    /**
     * Default constructor with accepts any behaviour.
     */
    public PatternFilter() {
        //initialize with array list for org.springframework.boot.context.propagationProperties.ConfigurationProperties compatibility.
        this(new ArrayList<>(asList(Pattern.compile(".*"))), new ArrayList<>());
    }

    /**
     * Initialize the {@link #includes} and {@link #excludes} with the arguments.
     *
     * @param includes the includes patterns to use.
     * @param excludes the excludes patterns to use.
     */
    public PatternFilter(@NotNull List<Pattern> includes, @NotNull List<Pattern> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    /**
     * Evaluates if the value is eligible.
     *
     * @param value the value to accept.
     * @return {@code true} when the value is eligible otherwise {@code false}.
     */
    @Override
    public boolean accept(String value) {
        return accept(value, includes) && !accept(value, excludes);
    }

    /**
     * Evaluates if the value is eligible against a list of patterns.
     *
     * @param value    the bean name
     * @param patterns the pattern list to evaluate against
     * @return @return {@code true} when the value is eligible to the patterns otherwise {@code false}.
     */
    private boolean accept(String value, List<Pattern> patterns) {
        Optional<Boolean> reduce = patterns.stream()
                .map(x -> x.matcher(value).find())
                .reduce((x, y) -> x || y);
        return reduce.isPresent() && reduce.get();
    }
}
