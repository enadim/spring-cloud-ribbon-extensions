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
import com.netflix.loadbalancer.Server;
import lombok.Setter;

import javax.validation.constraints.NotNull;

import static java.lang.String.format;

/**
 * Convenient support of predicate based rule.
 * <p>Defines a non final property predicate to satisfy the circular dependency between {@link PredicateBasedRule} and {@link AbstractServerPredicate}.
 *
 * @author Nadim Benabdenbi
 */
public class PredicateBasedRuleSupport extends PredicateBasedRule {

    /**
     * the rule description.
     */
    @Setter
    private RuleDescription description;
    /**
     * the delegate predicate.
     */
    @Setter
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
    public PredicateBasedRuleSupport(@NotNull AbstractServerPredicate predicate) {
        setPredicate(predicate);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractServerPredicate getPredicate() {
        return predicate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Server choose(Object key) {
        Server server = super.choose(key);
        if (server == null) {
            throw new ChooseServerException(format("There is so server satisfying rule %s.", description == null ? toString() : description.describe()));
        } else {
            return server;
        }
    }
}
