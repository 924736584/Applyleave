package com.Yfun.interview.util;


import org.apache.commons.lang.StringUtils;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.*;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * @ClassName : MailUntil
 * @Description :
 * @Author : DeYuan
 * @Date: 2020-09-03 16:22
 */
public class MailUtil {
    private Session mailSession;
    private static Properties properties = null;

    private void initJavaMail(Builder builder) {
        set("mail.transport.protocol", builder.protocol);
        set("mail.smtp.host", builder.host);
        set("mai.smtp.port", builder.port);
        set("mail.smtp.auth", builder.auth);
        //QQ:SSL安全认证
        set("mail.smtp.socktFactory.class", builder.sockFactory_class);
        set("mail.smtp.socketFactory.fallback", builder.sockFactory_fallback);
        set("mail.smtp.socketFactory.port", builder.sockFactory_port);
        this.mailSession = Session.getDefaultInstance(propertiesConfig());
    }

    public Session getMailSession() {
        if (mailSession == null) {
            throw new NullPointerException("Session not initialized ");
        }
        return mailSession;
    }

    private Properties propertiesConfig() {
        if (properties == null) {
            properties = new Properties();
        }
        return properties;
    }

    private void set(String properName, String properValue) {
        Properties config = propertiesConfig();
        if (StringUtils.isNotBlank(properValue)) {
            config.setProperty(properName, properValue);
        }
    }

    public static class Builder {
        /**
         * @Param protocol   使用的协议
         * @Param host  服务器的地址（域名）
         * @Param port 端口
         * @Param auth  需要授权
         * 支持SSl还需要以下类的支持
         * @Param SockFactory_class
         * java自带的支持的SSL验证的包
         * @See "mail.smtp.socktFactory.class","javax.net.ssl.SSLSocketFactory"
         * @Param SocketFactory_fallback
         * 是否处理不是ssl验证的文件默认一般false
         * @Param SocketFactory_port
         * 端口
         */
        private String protocol = "";
        private String host = "";
        private String port = "";
        private String auth = "";
        private String sockFactory_class = "javax.net.ssl.SSLSocketFactory";
        private String sockFactory_fallback = "false";
        private String sockFactory_port = "";

        public Builder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(String port) {
            this.port = port;
            return this;
        }

        public Builder auth(String auth) {
            this.auth = auth;
            return this;
        }

        public Builder sockFactory_class(String sockFactory_class) {
            sockFactory_class = sockFactory_class;
            return this;
        }

        public Builder sockFactory_fallback(String sockFactory_fallback) {
            sockFactory_fallback = sockFactory_fallback;
            return this;
        }

        public Builder sockFactory_port(String sockFactory_port) {
            sockFactory_port = sockFactory_port;
            return this;
        }

        public MailUtil build() {
            MailUtil mailUtil = new MailUtil();
            mailUtil.initJavaMail(this);
            return mailUtil;
        }

    }

    public static MimeMessage CreateMessage(MessageParameter messageP, MimeMultipart... appendixMulitpart) throws UnsupportedEncodingException, MessagingException {
        MimeMessage message = new MimeMessage(messageP.getSession());
        // 邮件：标题,正文,收件人,发件人
        message.setSubject(messageP.getSubject(), messageP.getText_code());
        Address addresses = new InternetAddress(messageP.getSenderMail(), messageP.getSenderName(), messageP.getText_code());
        message.setFrom(addresses);
        if (appendixMulitpart != null) {
            message.setContent(appendixMulitpart[0]);
        } else {
            message.setContent(messageP.getContent(), messageP.getContent_code());
        }
        // 收件人类型 To普通收件人
        Map<String, String> TORecipient = messageP.getTORecipientMail();
        // 收件人类型 CC 抄送收件人
        Map<String, String> CCRecipient = messageP.getCCRecipientMail();
        // 收件人类型 BCC 密送收件人
        Map<String, String> BCCRecipient = messageP.getBCCRecipientMail();

        if (!TORecipient.isEmpty()) {

            Set<String> keyset = TORecipient.keySet();
            Iterator iterator = keyset.iterator();
            while (iterator.hasNext()) {
                String RecipientMail = (String) iterator.next();
                String RecipientName = TORecipient.get(RecipientMail);
                message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(RecipientMail, RecipientName, messageP.getText_code()));
            }
        }
        if (!CCRecipient.isEmpty()) {
            Set<String> keyset = CCRecipient.keySet();
            Iterator iterator = keyset.iterator();
            while (iterator.hasNext()) {
                String RecipientMail = (String) iterator.next();
                String RecipientName = CCRecipient.get(RecipientMail);
                message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress(RecipientMail, RecipientName, messageP.getText_code()));
            }
        }
        if (!BCCRecipient.isEmpty()) {
            Set<String> keyset = BCCRecipient.keySet();
            Iterator iterator = keyset.iterator();
            while (iterator.hasNext()) {
                String RecipientMail = (String) iterator.next();
                String RecipientName = BCCRecipient.get(RecipientMail);
                message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(RecipientMail, RecipientName, messageP.getText_code()));
            }
        }
        message.setSentDate(messageP.getSendDate());
        message.saveChanges();
        return message;
    }

    public static class MessageParameter {

        private final Builder builder;

        public static class Builder {
            /**
             * @Param session   当前的会话
             * @Param senderName  发件人姓名
             * @Param senderMail 发件人邮箱
             * @Param TORecipientMail  普通收件人列表
             * @Param CCRecipientMail  抄送人集合
             * @Param BCCRecipientMail      密送人集合
             * @Param content       正文内容
             * @Param text_code 文档编码
             * @Param content_code 正文html编码
             * @Param sendDate 发送时间
             */
            private Session session;
            private String senderName;
            private String senderMail;
            private Map<String, String> TORecipientMail = new HashMap<>();
            private Map<String, String> CCRecipientMail = new HashMap<>();
            private Map<String, String> BCCRecipientMail = new HashMap<>();
            private String content;
            private String subject;
            private String text_code = "utf-8";
            private String content_code = "text/html;charset=utf-8";
            private Date sendDate = new Date();

            public Builder session(Session session) {
                this.session = session;
                return this;

            }

            public Builder senderName(String senderName) {
                this.senderName = senderName;
                return this;
            }

            public Builder senderMail(String senderMail) {
                this.senderMail = senderMail;
                return this;
            }

            public Builder TORecipientMail(Map<String, String> TORecipientMail) {
                this.TORecipientMail = TORecipientMail;
                return this;
            }

            public Builder CCRecipientMail(Map<String, String> CCRecipientMail) {
                this.CCRecipientMail = CCRecipientMail;
                return this;
            }

            public Builder BCCRecipientMail(Map<String, String> BCCRecipientMail) {
                this.BCCRecipientMail = BCCRecipientMail;
                return this;
            }

            public Builder content(String content) {
                this.content = content;
                return this;
            }

            public Builder subject(String subject) {
                this.subject = subject;
                return this;
            }

            public Builder text_code(String text_code) {
                this.text_code = text_code;
                return this;
            }

            public Builder Content_code(String content_code) {
                this.content_code = content_code;
                return this;
            }

            public Builder sendDate(Date sendDate) {
                this.sendDate = sendDate;
                return this;

            }

            public MessageParameter build() {
                return new MessageParameter(this);
            }
        }

        private MessageParameter(Builder builder) {
            this.builder = builder;
        }

        public Session getSession() {
            return builder.session;
        }

        public String getSenderName() {
            return builder.senderName;
        }

        public String getSenderMail() {
            return builder.senderMail;
        }

        public Map<String, String> getTORecipientMail() {
            return builder.TORecipientMail;
        }

        public Map<String, String> getCCRecipientMail() {
            return builder.CCRecipientMail;
        }

        public Map<String, String> getBCCRecipientMail() {
            return builder.BCCRecipientMail;
        }

        public String getContent() {
            return builder.content;
        }

        public String getSubject() {
            return builder.subject;
        }

        public String getText_code() {
            return builder.text_code;
        }

        public String getContent_code() {
            return builder.content_code;
        }

        public Date getSendDate() {
            return builder.sendDate;
        }
    }

    public static class AddEnclosure {
        private MessageParameter parameter;

        public AddEnclosure(MessageParameter parameter) {
            this.parameter = parameter;
        }

        public MimeMultipart Content_image(List<File> content_image, List<File> appendix_file) throws Exception {
            /**
             * 加载图片区
             */
            StringBuilder imgBuilder = new StringBuilder();
            MimeMultipart mm_text_image = new MimeMultipart();
            int fileSize = 0;
            if (content_image != null) {
                if (content_image.size() > 0)
                    for (File imgfile : content_image) {
                        fileSize += imgfile.length();
                    }
            }
            if (appendix_file != null) {
                if (appendix_file.size() > 0)
                    for (File imgfile : appendix_file) {
                        fileSize += imgfile.length();
                    }
            }
            if ((fileSize / (1024 * 1024)) < 50) {
                System.out.println("文件大小正常可以发送");
            } else
                throw new Exception("文本大小" + fileSize / (1024 * 1024) + "MB大于50MB无法发送");
            if (content_image != null) {
                if (content_image.size() > 0) {
                    for (File imgfile : content_image) {
                        MimeBodyPart image_BodyPart = new MimeBodyPart();
                        FileDataSource fileDataSource = new FileDataSource(imgfile);
                        DataHandler dataHandler = new DataHandler(fileDataSource);
                        image_BodyPart.setDataHandler(dataHandler);
                        image_BodyPart.setContentID(fileDataSource.getName());
                        image_BodyPart.setFileName(MimeUtility.encodeText(fileDataSource.getName()));
                        String showimage = "<img src=cid:'" + fileDataSource.getName() + "'   /><br/>";
                        imgBuilder.append(showimage);
                        mm_text_image.addBodyPart(image_BodyPart);
                    }
                }
            }

            /**
             * 文本区域
             */
            MimeBodyPart textPart = new MimeBodyPart();
            StringBuilder strBuid = new StringBuilder();
            if (StringUtils.isBlank(parameter.getContent())) {
                parameter.builder.content("");
            }
            strBuid.append(parameter.getContent());
            strBuid.append(imgBuilder);
            // strBuid.append("左边的就是图片");
            textPart.setContent(strBuid.toString(), parameter.getContent_code());
            mm_text_image.addBodyPart(textPart);
            // 关联模式
            mm_text_image.setSubType("related");
            /**
             * 注意，正文中只能出现MimeBodyPart普通的节点不能出现复合节点MimeMultipart
             * MimeMulttipart->MimeBodyPart
             */

            if (appendix_file == null) {
                return mm_text_image;
            } else {
                MimeBodyPart text_image_bodyPart = new MimeBodyPart();
                text_image_bodyPart.setContent(mm_text_image);
                MimeMultipart text_image_Multipart = new MimeMultipart();
                // 附件
                for (File file : appendix_file) {
                    MimeBodyPart attachment = new MimeBodyPart();
                    FileDataSource fileDataSource = new FileDataSource(file);
                    DataHandler attachmentHandler = new DataHandler(fileDataSource);
                    attachment.setDataHandler(attachmentHandler);
                    attachment.setFileName(MimeUtility.encodeText(fileDataSource.getName()));
                    text_image_Multipart.addBodyPart(attachment);
                }
                text_image_Multipart.addBodyPart(text_image_bodyPart);
                // 混合模式
                text_image_Multipart.setSubType("mixed");
                return text_image_Multipart;
            }
        }
    }
}
