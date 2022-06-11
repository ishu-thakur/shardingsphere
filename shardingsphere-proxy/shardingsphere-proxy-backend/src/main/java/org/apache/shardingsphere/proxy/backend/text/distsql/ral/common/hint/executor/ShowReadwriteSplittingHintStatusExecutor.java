/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.proxy.backend.text.distsql.ral.common.hint.executor;

import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.apache.shardingsphere.infra.merge.result.MergedResult;
import org.apache.shardingsphere.proxy.backend.response.header.query.QueryHeader;
import org.apache.shardingsphere.proxy.backend.text.distsql.ral.common.hint.HintSourceType;
import org.apache.shardingsphere.readwritesplitting.distsql.parser.statement.hint.ShowReadwriteSplittingHintStatusStatement;
import org.apache.shardingsphere.infra.merge.result.impl.local.LocalDataMergedResult;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Show readwrite-splitting hint status executor.
 */
@RequiredArgsConstructor
public final class ShowReadwriteSplittingHintStatusExecutor extends AbstractHintQueryExecutor<ShowReadwriteSplittingHintStatusStatement> {
    
    @Override
    protected List<QueryHeader> createQueryHeaders() {
        List<QueryHeader> result = new ArrayList<>(2);
        result.add(new QueryHeader("", "", "source", "", Types.CHAR, "CHAR", 5, 0, false, false, false, false));
        return result;
    }
    
    @Override
    protected MergedResult createMergedResult() {
        List<Object> row = createRow(HintManager.isWriteRouteOnly() ? HintSourceType.WRITE.getValue() : HintSourceType.AUTO.getValue());
        return new LocalDataMergedResult(Collections.singletonList(row));
    }
    
    private List<Object> createRow(final String source) {
        List<Object> result = new ArrayList<>(1);
        result.add(source);
        return result;
    }
}
