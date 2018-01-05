Androids
======

Androids是本人根据平时的项目实践经验，为了提高Android开发效率而写的一个工具SDK；里面提供了一些工具类以及自定义View，可在实际项目开发时直接使用。

![ANDROIDS](https://raw.githubusercontent.com/1993hzw/common/master/Androids/androids.png)

### 使用
```
dependencies {
    compile 'com.forward.androids:androids:1.1.7.1'
}
```
### 项目结构
#### --AndroidsDemo　使用例子

#### --androids 库工程

### 自定义View
  
  * #### ShapeImageView
  可设置形状(圆形、圆角矩形)的ImageView
  
  [点击查看示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_shapeimageview.xml)
  
  ```xml
  <attr name="siv_shape" format="enum">
      <enum name="rect" value="1"/>
      <enum name="circle" value="2"/>
      <enum name="oval" value="3"/>
  </attr>
  <attr name="siv_round_radius" format="dimension"/>
  <attr name="siv_round_radius_leftTop" format="dimension"/>
  <attr name="siv_round_radius_leftBottom" format="dimension"/>
  <attr name="siv_round_radius_rightTop" format="dimension"/>
  <attr name="siv_round_radius_rightBottom" format="dimension"/>
  <attr name="siv_border_size" format="dimension"/>
  <attr name="siv_border_color" format="color"/>
  ```

  [《Android自定义View——可设置形状(圆形、圆角矩形、椭圆)的ImageView，抗锯齿》](http://blog.csdn.net/u012964944/article/details/50548720)
  
  * #### MaskImageView
  可在背景图和前景图显示遮罩效果的ImageView
  
  [点击查看示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_maskimageview.xml)
  
  ```xml
  <!-- 遮罩的层面:背景\前景图-->
  <attr name="miv_mask_level" format="enum">
      <enum name="background" value="1"/>
      <enum name="foreground" value="2"/>
  </attr>
  <!-- 设置了setClickable(true)才生效,默认开启遮罩-->
  <attr name="miv_is_show_mask_on_click" format="boolean"/>
  <attr name="miv_mask_color" format="color"/>
  <!--是否忽略图片的透明度，默认为true,透明部分不显示遮罩 -->
  <attr name="miv_is_ignore_alpha" format="boolean"/>
  ```

  [《Android自定义View——可在背景图和前景图显示遮罩效果的ImageView》](http://blog.csdn.net/u012964944/article/details/50560503)

  * #### STextView/SLayout
  可直接在布局文件设置shape和selector，实现点击效果
  
  [点击查看示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_maskimageview.xml)
  
  STextView支持的属性
  ```xml
  <attr name="stv_text_color_selected" format="color"/>
  <attr name="stv_text_color_pressed" format="color"/>
  <attr name="stv_text_color_disable" format="color"/>
  ```
  
  STextView/SLayout支持的属性
  ```xml
  <!-- 背景形状 -->
  <attr name="sel_background_shape" format="enum">
      <enum name="rect" value="0"/>
      <enum name="oval" value="1"/>
      <enum name="line" value="2"/>
      <enum name="ring" value="3"/>
  </attr>
  <!-- 背景圆角 -->
  <attr name="sel_background_corners" format="dimension"/>
  <attr name="sel_background_corner_topLeft" format="dimension"/>
  <attr name="sel_background_corner_topRight" format="dimension"/>
  <attr name="sel_background_corner_bottomLeft" format="dimension"/>
  <attr name="sel_background_corner_bottomRight" format="dimension"/>
  <!-- 背景状态 -->
  <attr name="sel_background_pressed" format="reference|color"/>
  <attr name="sel_background_selected" format="reference|color"/>
  <!-- 背景边框 -->
  <attr name="sel_background_border_color" format="color"/>
  <attr name="sel_background_border_width" format="dimension"/>
  <!-- 背景边框状态 -->
  <attr name="sel_background_border_pressed" format="color"/>
  <attr name="sel_background_border_selected" format="color"/>
  ```
  
  * #### RatioImageView
  可以设置宽高比例的ImageView
  
  [点击查看示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_ratioimageview.xml)
  
  ```xml
  <!-- 宽度是否根据src图片的比例来测量（高度已知） -->
  <attr name="riv_is_width_fix_drawable_size_ratio" format="boolean"/>
  <!-- 高度是否根据src图片的比例来测量（宽度已知） -->
  <attr name="riv_is_height_fix_drawable_size_ratio" format="boolean"/>
  <!--当mIsWidthFitDrawableSizeRatio生效时，最大宽度-->
  <attr name="riv_max_width_when_width_fix_drawable" format="dimension"/>
  <!--当mIsHeightFitDrawableSizeRatio生效时-->
  <attr name="riv_max_height_when_height_fix_drawable" format="dimension"/>
  <!-- 高度设置，参考宽度，如0.5 , 表示 高度＝宽度×０.5 -->
  <attr name="riv_height_to_width_ratio" format="float"/>
  <!-- 宽度设置，参考高度，如0.5 , 表示 宽度＝高度×０.5 -->
  <attr name="riv_width_to_height_ratio" format="float"/>
  <!--宽度和高度,避免layout_width/layout_height会在超过屏幕尺寸时特殊处理的情况-->
  <attr name="riv_width" format="dimension"/>
  <attr name="riv_height" format="dimension"/>
  ```

  [《Android自定View——可以设置宽高比例的ImageView》](http://blog.csdn.net/u012964944/article/details/50600078)
  
  * #### ScrollPickerView
  滚动选择器，支持循环滚动，可实现生日选择器，老虎机等
  
```xml
   <declare-styleable name="ScrollPickerView">
        <attr name="spv_center_item_background" format="reference|color"/>
        <attr name="spv_visible_item_count" format="integer"/>
        <!-- 中间item的位置,默认为 mVisibleItemCount / 2-->
        <attr name="spv_center_item_position" format="integer"/>
        <!-- 是否循环滚动，默认为true，开启-->
        <attr name="spv_is_circulation" format="boolean"/>
        <!-- 不允许父组件拦截触摸事件，设置为true为不允许拦截，此时该设置才生效 -->
        <attr name="spv_disallow_intercept_touch" format="boolean"/>
        <!-- 滚动的方向-->
        <attr name="spv_orientation" format="string">
            <enum name="horizontal" value="1"/>
            <enum name="vertical" value="2"/>
        </attr>
    </declare-styleable>

    <declare-styleable name="StringScrollPicker">
        <attr name="spv_min_text_size" format="dimension"/>
        <attr name="spv_max_text_size" format="dimension"/>
        <attr name="spv_start_color" format="color"/>
        <attr name="spv_end_color" format="color"/>
        <attr name="spv_max_line_width" format="dimension"/>
        <attr name="spv_alignment" format="enum">
            <enum name="center" value="1"/>
            <enum name="left" value="2"/>
            <enum name="right" value="3"/>
        </attr>
    </declare-styleable>

    <declare-styleable name="BitmapScrollPicker">
        <!-- 绘制图片的方式-->
        <attr name="spv_draw_bitmap_mode" format="string">
            <enum name="fill" value="1"/>
            <enum name="center" value="2"/>
            <enum name="size" value="3"/>
        </attr>
        <!-- 绘制图片的方式为size时，指定的图片绘制大小-->
        <attr name="spv_draw_bitmap_width" format="dimension"/>
        <attr name="spv_draw_bitmap_height" format="dimension"/>
        <!-- item内容缩放倍数-->
        <attr name="spv_min_scale" format="float"/>
        <attr name="spv_max_scale" format="float"/>
    </declare-styleable>
```
  
  [点击查看示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/src/com/example/androidsdemo/ScrollPickerViewDemo.java)

  [《 Android自定义view——滚动选择器》](http://blog.csdn.net/u012964944/article/details/50847973)
 
  [《 android图片滚动选择器的实现》](http://blog.csdn.net/u012964944/article/details/70172885)
 
  [《 Android滚动选择器——水平滚动》](http://blog.csdn.net/u012964944/article/details/73189206)
    
  * #### KeyboardLayout
  监听输入法键盘的弹起与隐藏，可实现输入法和工具栏无缝切换
  
  [点击查看示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/src/com/example/androidsdemo/KeyboardLayoutDemo.java)

  [《Android获取输入法高度——输入法与页面布局无缝切换》](http://blog.csdn.net/u012964944/article/details/52120726)
  
  * #### DragListView
  可拖拽的ListView，拖拽排序
  
  [点击查看示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/src/com/example/androidsdemo/DragListViewDemo.java)

  [《Android自定义View——可拖拽的ListView》](http://blog.csdn.net/u012964944/article/details/52086674)
  
### 工具类

* #### 图片加载器
[点击查看示例代码](https://github.com/1993hzw/ImageSelector/blob/master/library/src/main/java/cn/hzw/imageselector/ImageLoader.java)

* #### AnimatorUtil
  对AnimatorSet进行封装，便以链式构建动画；支持设置循环次数
  
  [点击查看示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/src/com/example/androidsdemo/AnimatorUtilDemo.java)

  [《Android属性动画封装之快速构建动画》](http://blog.csdn.net/u012964944/article/details/50854430)

* #### 其他
  * [LogUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/LogUtil.java) 日志输出
  * [ProcessUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/ProcessUtil.java) 进程相关
  * [ReflectUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/ReflectUtil.java) 反射相关
  * [StatusBarUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/StatusBarUtil.java) 设置页面为沉浸式状态栏
  * [ThreadUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/ThreadUtil.java) 线程相关，包括主线程和子线程
  * [DateUtil](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/DateUtil.java) 日期相关
  * [Util](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/Util.java)
### 更新

* v1.1.7.1（11） 

1.修复SelectorAttrs中background_border的bug。

* v1.1.7（10） 

1.完善SelectorAttrs逻辑，支持设置图片。

2.增加TouchGestureDetector

* v1.1.6（9） 

文字滚动选择器支持文字换行.
