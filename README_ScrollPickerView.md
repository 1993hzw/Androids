ScrollPickerView
======
滚动选择器，支持循环滚动，可实现生日选择器，老虎机等

![01](https://raw.githubusercontent.com/1993hzw/common/master/Androids/scrollpicker01.gif)

![02](https://raw.githubusercontent.com/1993hzw/common/master/Androids/scrollpicker02.gif)

* 支持的View

`StringScrollPicker`

`BitmapScrollPicker`

* 示例

```xml
<cn.forward.androids.views.BitmapScrollPicker
        android:id="@+id/picker"
        android:layout_width="wrap_content"
        android:layout_height="220dp"
        android:layout_weight="1"
        android:background="#888888"
        app:spv_disallow_intercept_touch="true"
        app:spv_draw_bitmap_height="50dp"
        app:spv_draw_bitmap_mode="center"
        app:spv_draw_bitmap_width="50dp"
        app:spv_is_circulation="false"
        app:spv_max_scale="1.2"
        app:spv_min_scale="0.6"
        app:spv_visible_item_count="3"
        />
```

```java
BitmapScrollPicker picker = findViewById(R.id.picker);
picker.setData(bitmaps);
picker.setOnSelectedListener(new ScrollPickerView.OnSelectedListener() {
    @Override
    public void onSelected(ScrollPickerView scrollPickerView, int position) {
        // do something
    }
});

```

[点击查看更多示例代码](https://github.com/1993hzw/Androids/blob/master/AndroidsDemo/src/com/example/androidsdemo/ScrollPickerViewDemo.java)


* 相关属性

```xml
<!--滚动选择器通用属性-->
<declare-styleable name="ScrollPickerView">
    <!-- 中间item的背景-->
    <attr name="spv_center_item_background" format="reference|color"/>
    <!-- 可见的item数量，默认为3个-->
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

<!--文字选择器-->
<declare-styleable name="StringScrollPicker">
    <!--文字渐变大小-->
    <attr name="spv_min_text_size" format="dimension"/>
    <attr name="spv_max_text_size" format="dimension"/>
    <!--文字渐变颜色-->
    <attr name="spv_start_color" format="color"/>
    <attr name="spv_end_color" format="color"/>
    <!--文字最大行宽-->
    <attr name="spv_max_line_width" format="dimension"/>
    <!--文字对齐方式-->
    <attr name="spv_alignment" format="enum">
        <enum name="center" value="1"/>
        <enum name="left" value="2"/>
        <enum name="right" value="3"/>
    </attr>
</declare-styleable>

<!--图片选择器-->
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

* 更多

[《 Android自定义view——滚动选择器》](http://blog.csdn.net/u012964944/article/details/50847973)

[《 android图片滚动选择器的实现》](http://blog.csdn.net/u012964944/article/details/70172885)

[《 Android滚动选择器——水平滚动》](http://blog.csdn.net/u012964944/article/details/73189206)