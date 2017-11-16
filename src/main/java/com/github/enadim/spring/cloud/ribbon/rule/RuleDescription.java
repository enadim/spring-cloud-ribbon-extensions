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
package com.github.enadim.spring.cloud.ribbon.rule;

import com.netflix.loadbalancer.AbstractServerPredicate;

import static java.lang.String.format;

/**
 * Rule Convenient Description
 *
 * @see PredicateBasedRuleSupport for concrete usage.
 */
@FunctionalInterface
public interface RuleDescription {
    /**
     * @return the rule description
     */
    String describe();

    /**
     * Constructs a rule description from a given static parameter.
     *
     * @param description the static rule description.
     * @return the rule description.
     */
    static RuleDescription from(String description) {
        return () -> description;
    }

    /**
     * Constructs a rule description from a predicate.
     *
     * @param predicate the predicate.
     * @return the predicate rule description.
     */
    static RuleDescription from(AbstractServerPredicate predicate) {
        return predicate::toString;
    }

    /**
     * Derive a Rule description with an AND operator.
     *
     * @param rightOperand the right operand description.
     * @return the composite AND rule description.
     */
    default RuleDescription and(RuleDescription rightOperand) {
        return () -> format("(%s && %s)", describe(), rightOperand.describe());
    }

    /**
     * Derive a Rule description with a fallback operator.
     *
     * @param fallback the fallback description.
     * @return the composite fallback rule description.
     */
    default RuleDescription fallback(RuleDescription fallback) {
        return () -> format("%s -> %s", describe(), fallback.describe());
    }
}
