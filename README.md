Androids
======

### 使用
```
dependencies {
    compile 'com.forward.androids:androids:1.1.7'
}
```

### --AndroidsDemo　使用例子

### --androids 库工程
  
  * #### ShapeImageView
  可设置形状(圆形、圆角矩形)的ImageView
  
  [示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_shapeimageview.xml)
  
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
  
  [示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_maskimageview.xml)
  
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
  
  [示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_maskimageview.xml)
  
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
  
  [示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_ratioimageview.xml)
  
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
  
  [示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/src/com/example/androidsdemo/ScrollPickerViewDemo.java)

  [《 Android自定义view——滚动选择器》](http://blog.csdn.net/u012964944/article/details/50847973)
 
  [《 android图片滚动选择器的实现》](http://blog.csdn.net/u012964944/article/details/70172885)
 
  [《 Android滚动选择器——水平滚动》](http://blog.csdn.net/u012964944/article/details/73189206)
    
  * #### AnimatorUtil
  对AnimatorSet进行封装，便以链式构建动画；支持设置循环次数
  
  [示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/src/com/example/androidsdemo/AnimatorUtilDemo.java)

  [《Android属性动画封装之快速构建动画》](http://blog.csdn.net/u012964944/article/details/50854430)

  * #### KeyboardLayout
  监听输入法键盘的弹起与隐藏，可实现输入法和工具栏无缝切换
  
  [示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/src/com/example/androidsdemo/KeyboardLayoutDemo.java)

  [《Android获取输入法高度——输入法与页面布局无缝切换》](http://blog.csdn.net/u012964944/article/details/52120726)
  
  * #### DragListView
  可拖拽的ListView，拖拽排序
  
  [示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/src/com/example/androidsdemo/DragListViewDemo.java)

  [《Android自定义View——可拖拽的ListView》](http://blog.csdn.net/u012964944/article/details/52086674)
  

### 更新

* v1.1.7（10） 

1.完善SelectorAttrs逻辑，支持设置图片。

2.增加TouchGestureDetector

* v1.1.6（9） 

文字滚动选择器支持文字换行.
