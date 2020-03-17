/*
 * Copyright (C) 2020 xuexiangjys(xuexiangjys@163.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.lightingstar.appcontroller.util;

import com.lightingstar.appcontroller.model.TaskInfo;
import com.xuexiang.xaop.annotation.MemoryCache;

import java.util.ArrayList;
import java.util.List;

/**
 * 演示数据
 *
 * @author lightingstar
 * @since 2018/11/23 下午5:52
 */
public class MockDataProvider {

    /**
     * 用于占位的空信息
     *
     * @return
     */
    @MemoryCache
    public static List<TaskInfo> getTaskInfos() {
        List<TaskInfo> list = new ArrayList<>();
        TaskInfo taskInfo1 =  new TaskInfo();
        taskInfo1.setTaskName("任务1");
        taskInfo1.setScore("1分");
        list.add(taskInfo1);

        TaskInfo taskInfo2 =  new TaskInfo();
        taskInfo2.setTaskName("任务2");
        taskInfo2.setScore("1分");
        list.add(taskInfo2);

        TaskInfo taskInfo3 =  new TaskInfo();
        taskInfo3.setTaskName("任务3");
        taskInfo3.setScore("1分");
        list.add(taskInfo3);

        TaskInfo taskInfo4 =  new TaskInfo();
        taskInfo4.setTaskName("任务4");
        taskInfo4.setScore("1分");
        list.add(taskInfo4);

        TaskInfo taskInfo5 =  new TaskInfo();
        taskInfo5.setTaskName("任务5");
        taskInfo5.setScore("1分");
        list.add(taskInfo5);

        TaskInfo taskInfo6 =  new TaskInfo();
        taskInfo6.setTaskName("任务6");
        taskInfo6.setScore("1分");
        list.add(taskInfo6);

        TaskInfo taskInfo7 =  new TaskInfo();
        taskInfo7.setTaskName("任务7");
        taskInfo7.setScore("1分");
        list.add(taskInfo7);

        TaskInfo taskInfo8 =  new TaskInfo();
        taskInfo8.setTaskName("任务8");
        taskInfo8.setScore("1分");
        list.add(taskInfo8);

        TaskInfo taskInfo9 =  new TaskInfo();
        taskInfo9.setTaskName("任务9");
        taskInfo9.setScore("1分");
        list.add(taskInfo9);

        TaskInfo taskInfo10 =  new TaskInfo();
        taskInfo10.setTaskName("任务10");
        taskInfo10.setScore("1分");
        list.add(taskInfo10);

        TaskInfo taskInfo11 =  new TaskInfo();
        taskInfo11.setTaskName("任务11");
        taskInfo11.setScore("1分");
        list.add(taskInfo11);


        return list;
    }


}
