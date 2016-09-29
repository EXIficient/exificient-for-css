# exificient-for-css

EXI for CSS (Explorative) - How EXI can be used to represent CSS efficiently w.r.t.

* Size and
* Processing speed

[![Build Status](https://travis-ci.org/EXIficient/exificient-for-css.svg?branch=master)](https://travis-ci.org/EXIficient/exificient-for-css)


## Document Object Model (DOM) Style Specification

We use the [CSS Parser](http://cssparser.sourceforge.net/) which reads Cascading Style Sheets (CSS3, CSS2.1, CSS2, and CSS1) and generates
a [CSS Object Model (CSSOM)](https://www.w3.org/TR/cssom-1/).

By means of an XML schema we then efficiently represent the style information with EXI.

On the contrary the efficient representation can be transformed back to plain-text CSS, DOM Style, or alternatively provided as events.


## Sample

### CSS

```css
body {
    background-color: #d0e4fe;
}
h1 {
    color: orange;
    text-align: center;
}
p {
    font-family: "Times New Roman";
    font-size: 20px;
}
```

### XML (efficiently represented with EXI)

```xml
<stylesheet>
    <cssStyleRule>
        <selectorText>body</selectorText>
        <style>
            <property>background-color</property>
            <cssValue>
                <cssPrimitiveValue>
                    <cssRgbColor>
                        <r>208</r>
                        <g>228</g>
                        <b>254</b>
                    </cssRgbColor>
                </cssPrimitiveValue>
            </cssValue>
        </style>
    </cssStyleRule>
    <cssStyleRule>
        <selectorText>h1</selectorText>
        <style>
            <property>color</property>
            <cssValue>
                <cssPrimitiveValue>
                    <cssIdent>orange</cssIdent>
                </cssPrimitiveValue>
            </cssValue>
            <property>text-align</property>
            <cssValue>
                <cssPrimitiveValue>
                    <cssIdent>center</cssIdent>
                </cssPrimitiveValue>
            </cssValue>
        </style>
    </cssStyleRule>
    <cssStyleRule>
        <selectorText>p</selectorText>
        <style>
            <property>font-family</property>
            <cssValue>
                <cssPrimitiveValue>
                    <cssString>Times New Roman</cssString>
                </cssPrimitiveValue>
            </cssValue>
            <property>font-size</property>
            <cssValue>
                <cssPrimitiveValue>
                    <cssPx>20.0</cssPx>
                </cssPrimitiveValue>
            </cssValue>
        </style>
    </cssStyleRule>
</stylesheet>
```
