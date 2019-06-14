SView
======
可直接在xml布局文件设置shape和selector，实现点击效果

![SView](https://raw.githubusercontent.com/1993hzw/common/master/Androids/sview.png)

* 支持的View

`STextView`

`MaskImageView`

`ShapeImageView`

`SFrameLayout`

`SLinearLayout`

`SRelativeLayout`

* 示例

```xml
<cn.forward.androids.views.STextView
    android:layout_width="85dp"
    android:layout_height="85dp"
    android:background="#00ff00"
    android:clickable="true"
    android:gravity="center"
    android:text="STextView"
    android:textColor="#000000"
    app:sel_background_border_color="#0000ff"
    app:sel_background_border_pressed="#888888"
    app:sel_background_border_width="4dp"
    app:sel_background_pressed="#ffffff"
    app:sel_background_shape="oval" />
```

[点击查看更多示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_maskimageview.xml)


* 相关属性

```xml
<!-- 背景状态 -->
<attr name="sel_background_pressed" format="reference|color" />
<attr name="sel_background_selected" format="reference|color" />
<attr name="sel_background_disable" format="reference|color" />

<!-- 背景形状 -->
<attr name="sel_background_shape" format="enum">
    <enum name="rect" value="0" />
    <enum name="oval" value="1" />
    <enum name="line" value="2" />
    <enum name="ring" value="3" />
</attr>
<!-- 背景圆角 -->
<attr name="sel_background_corners" format="dimension" />
<attr name="sel_background_corner_topLeft" format="dimension" />
<attr name="sel_background_corner_topRight" format="dimension" />
<attr name="sel_background_corner_bottomLeft" format="dimension" />
<attr name="sel_background_corner_bottomRight" format="dimension" />

<!-- 背景边框默认状态 -->
<attr name="sel_background_border_color" format="color" />
<attr name="sel_background_border_width" format="dimension" />
<!-- 背景边框状态 -->
<attr name="sel_background_border_pressed" format="color" />
<attr name="sel_background_border_selected" format="color" />
<attr name="sel_background_border_disable" format="color" />

<!-- ripple水波纹效果-->
<attr name="sel_background_ripple" format="reference|color" />
<attr name="sel_background_ripple_mask" format="reference|color" />
<!-- ripple mask形状(仅在sel_background_ripple_mask为color时生效)-->
<attr name="sel_background_ripple_mask_shape" format="enum">
    <enum name="rect" value="0" />
    <enum name="oval" value="1" />
    <enum name="line" value="2" />
    <enum name="ring" value="3" />
</attr>
<!-- ripple mask圆角 -->
<attr name="sel_background_ripple_mask_corners" format="dimension" />
<attr name="sel_background_ripple_mask_corner_topLeft" format="dimension" />
<attr name="sel_background_ripple_mask_corner_topRight" format="dimension" />
<attr name="sel_background_ripple_mask_corner_bottomLeft" format="dimension" />
<attr name="sel_background_ripple_mask_corner_bottomRight" format="dimension" />

<!--该属性已过时，被废弃-->
<attr name="sel_background" format="color" />
```