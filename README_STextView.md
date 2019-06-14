STextView
======
可直接在xml布局文件设置文字点击效果

* 示例

```xml
 <cn.forward.androids.views.STextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:clickable="true"
        android:gravity="center"
        android:text="STextView"
        android:textColor="#000000"
        app:stv_text_color_disable="#888888"
        app:stv_text_color_pressed="#ff0000"
        app:stv_text_color_selected="#ff8800" />
```

* 相关属性

```xml
<attr name="stv_text_color_selected" format="color"/>
<attr name="stv_text_color_pressed" format="color"/>
<attr name="stv_text_color_disable" format="color"/>
```

* 更多

`STextView`还支持在xml布局文件设置shape和selector，实现点击效果，[点击了解更多]()