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
import com.netflix.loadbalancer.PredicateBasedRule;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

/**
 * Convenient support of predicate based rule.
 * <p>Defines a non final property predicate to satisfy the circular dependency between {@link PredicateBasedRule} and {@link AbstractServerPredicate}.
 *
 * @author Nadim Benabdenbi
 */
public class PredicateBasedRuleSupport extends PredicateBasedRule {
    /**
     * the delegate predicate.
     */
    @Inject
    private AbstractServerPredicate predicate;

    /**
     * Creates new default instance
     */
    public PredicateBasedRuleSupport() {
        super();
    }

    /**
     * Creates new instance of {@link PredicateBasedRuleSupport} class with specific predicate.
     *
     * @param predicate the server predicate, can't be null
     * @throws IllegalArgumentException if {@code predicate} is {@code null}
     */
    public PredicateBasedRuleSupport(AbstractServerPredicate predicate) {
        setPredicate(predicate);
    }

    /**
     * Convenient delegate predicate setter.
     *
     * @param predicate the rule predicate.
     */
    public void setPredicate(@NotNull AbstractServerPredicate predicate) {
        this.predicate = predicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractServerPredicate getPredicate() {
        return predicate;
    }
}
