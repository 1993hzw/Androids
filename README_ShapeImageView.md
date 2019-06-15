ShapeImageView
======
可设置形状(圆形、圆角矩形)的ImageView

![SView](https://raw.githubusercontent.com/1993hzw/common/master/Androids/shapeimage.png)

* 示例

```xml
<cn.forward.androids.views.ShapeImageView
    android:layout_width="110dp"
    android:layout_height="110dp"
    android:layout_marginLeft="5dp"
    android:src="@drawable/world_map"
    app:siv_round_radius="25dp"
    app:siv_shape="rect" />
```

[点击查看更多示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/res/layout/activity_shapeimageview.xml)

* 相关属性

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

* 更多

`ShapeImageView`还支持在xml布局文件设置shape和selector，实现点击效果，[点击了解更多](https://github.com/1993hzw/Androids/blob/master/README_SView.md)

[《Android自定义View——可设置形状(圆形、圆角矩形、椭圆)的ImageView，抗锯齿》](http://blog.csdn.net/u012964944/article/details/50548720)