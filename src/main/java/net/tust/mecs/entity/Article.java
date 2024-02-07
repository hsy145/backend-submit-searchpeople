package net.tust.mecs.entity;

import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
public class Article {
    @NotBlank
    private String title;//	是	标题
    private String author;//	否	作者
    private String digest;// 	否	图文消息的摘要，仅有单图文消息才有摘要，多图文此处为空。如果本字段为没有填写，则默认抓取正文前54个字。
    @NotBlank
    private String content;	//是	图文消息的具体内容，支持HTML标签，必须少于2万字符，小于1M，且此处会去除JS,涉及图片url必须来源 "上传图文消息内的图片获取URL"接口获取。外部图片url将被过滤。
    private String content_source_url;//	否	图文消息的原文地址，即点击“阅读原文”后的URL
    @NotBlank
    private String thumb_media_id;//	是	图文消息的封面图片素材id（必须是永久MediaID）
    private Integer need_open_comment=0;	//否	Uint32 是否打开评论，0不打开(默认)，1打开
    private Integer only_fans_can_comment=0;//	否	Uint32 是否粉丝才可评论，0所有人可评论(默认)，1粉丝才可评论
    private  String pic_crop_235_1;	//否	封面裁剪为2.35:1规格的坐标字段。以原始图片（thumb_media_id）左上角（0,0），右下角（1，1）建立平面坐标系，经过裁剪后的图片，其左上角所在的坐标即为（X1,Y1）,右下角所在的坐标则为（X2,Y2）。
    private String pic_crop_1_1;	//否	封面裁剪为1:1规格的坐标
}
