MaskImageView
======
可很对背景图或前景图显示遮罩效果的ImageView

![01](https://raw.githubusercontent.com/1993hzw/common/master/Androids/maskimageview.jpeg)

* 示例

```xml
<cn.forward.androids.views.MaskImageView
    android:layout_width="100dp"
    android:layout_height="100dp"
    android:background="@drawable/bg"
    android:clickable="true"
    android:scaleType="fitXY"
    android:src="@drawable/ic_launcher"
    app:miv_is_ignore_alpha="true"
    app:miv_mask_color="#880000ff"
    app:miv_mask_level="foreground" />
```

[点击查看更多示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_maskimageview.xml)


* 相关属性

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

* 更多

`MaskImageView`还支持在xml布局文件设置shape和selector，实现点击效果，[点击了解更多](https://github.com/1993hzw/Androids/blob/master/README_SView.md)

[PaddingView](https://github.com/1993hzw/Androids/blob/master/README_PaddingView.md)

[《Android自定义View——可在背景图和前景图显示遮罩效果的ImageView》](http://blog.csdn.net/u012964944/article/details/50560503)