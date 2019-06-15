EllipsizeUtils
======
文字截断和高亮，支持多行和根据关键字省略

![01](https://raw.githubusercontent.com/1993hzw/common/master/Androids/ellipsizeutils.jpeg)

* 示例

```java
// 根据关键字进行截断，并对关键字高亮处理
EllipsizeUtils.ellipsizeAndHighlight(mTextView, text, keyword, Color.RED, false, false);
// 文字截断，支持多行，支持start\middle\end裁剪
EllipsizeUtils.ellipsize(mTextView, text);
```

* 更多

[EllipsizeUtils源码](https://github.com/1993hzw/Androids/blob/master/androids/src/cn/forward/androids/utils/EllipsizeUtils.java)