PaddingView
======
在xml布局文件设置View的内容padding, 支持靠边对齐。用于解决点击区域大于内容区域的问题

* 支持的View

`PaddingImageView`

`MaskImageView`

`PaddingTextView`

`STextView`

* 示例

```xml
<cn.forward.androids.views.MaskImageView
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:background="#00ffff"
        android:clickable="true"
        android:scaleType="fitXY"
        android:src="@drawable/ic_launcher"
        app:vp_content_height="60dp"
        app:vp_content_padding_bottom="0dp"
        app:vp_content_padding_left="0dp"
        app:vp_content_width="60dp" />
```

![01](https://raw.githubusercontent.com/1993hzw/common/master/Androids/paddingview.png)

* 相关属性

```xml
<!--ImageView的padding相关设置, layout_width/height为精确值下列属性才生效-->
<declare-styleable name="ViewPaddingAttrs">
    <!--src content的确切宽度，必填-->
    <attr name="vp_content_width" format="dimension" />
    <!--src content的确切高度，必填-->
    <attr name="vp_content_height" format="dimension" />
    <!--src content的padding，在未设置的情况下默认为居中效果-->
    <attr name="vp_content_padding_left" format="dimension" />
    <attr name="vp_content_padding_top" format="dimension" />
    <attr name="vp_content_padding_right" format="dimension" />
    <attr name="vp_content_padding_bottom" format="dimension" />
</declare-styleable>
```