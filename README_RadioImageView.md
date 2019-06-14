RadioImageView
======
可以设置宽高比例的ImageView

![01](https://raw.githubusercontent.com/1993hzw/common/master/Androids/radioimageview.png)

* 示例

```xml
<cn.forward.androids.views.RatioImageView
        android:layout_width="wrap_content"
        android:layout_height="100dp"
        android:scaleType="fitXY"
        android:src="@drawable/world_map"
        app:riv_width_to_height_ratio="1.8" />
```

[点击查看更多示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_ratioimageview.xml)


* 相关属性

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

* 更多

`RadioImageView`还支持在xml布局文件设置shape和selector，实现点击效果，[点击了解更多]()

[《Android自定View——可以设置宽高比例的ImageView》](http://blog.csdn.net/u012964944/article/details/50600078)