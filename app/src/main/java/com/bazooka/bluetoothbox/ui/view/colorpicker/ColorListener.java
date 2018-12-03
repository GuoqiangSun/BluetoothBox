
/*
 * Copyright (C) 2017 skydoves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bazooka.bluetoothbox.ui.view.colorpicker;

/**
 * 颜色选择器监听器
 */
public interface ColorListener {
    /**
     * 颜色改变
     * @param color 当前颜色
     */
    void onColorSelected(int color);

    /**
     * 当触摸停止，即抬起手指时回调
     * @param color 当前颜色
     */
    void onTouchStop(int color);
}
