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

package com.lightingstar.appcontroller.adapter;

import com.lightingstar.appcontroller.R;
import com.lightingstar.appcontroller.model.AppBasicInfo;
import com.scwang.smartrefresh.layout.adapter.SmartRecyclerAdapter;
import com.scwang.smartrefresh.layout.adapter.SmartViewHolder;
import com.xuexiang.xui.widget.imageview.RadiusImageView;

import java.util.Collection;

/**
 * 首页动态新闻【只是用于演示效果】
 *
 * @author XUE
 * @since 2019/5/9 10:41
 */
public class AppViewListAdapter extends SmartRecyclerAdapter<AppBasicInfo> {

    public AppViewListAdapter() {
        super(R.layout.app_list_item);
    }

    public AppViewListAdapter(Collection<AppBasicInfo> data) {
        super(data, R.layout.app_list_item);
    }

    /**
     * 绑定布局控件
     *
     * @param holder
     * @param model
     * @param position
     */
    @Override
    protected void onBindViewHolder(SmartViewHolder holder, AppBasicInfo model, int position) {
        holder.text(R.id.app_name, model.getName());
        RadiusImageView image = holder.findViewById(R.id.app_icon);
        image.setImageDrawable(model.getAppIcon());
        holder.checked(R.id.app_forbidden,model.isChecked());
        holder.textColorId(R.id.app_name, R.color.xui_config_color_light_blue_gray);
    }
}
