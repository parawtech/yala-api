package tech.rket.storage.domain.value_object;

import tech.rket.shared.core.domain.DomainObject;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Getter
public enum MimeType implements DomainObject.ValueObject {
    // Text File Types
    TEXT_PLAIN("text/plain", "Plain Text File", Category.TEXT),
    TEXT_HTML("text/html", "HTML File", Category.TEXT),
    TEXT_CSV("text/csv", "CSV File", Category.TEXT),

    // Image File Types
    IMAGE_JPEG("image/jpeg", "JPEG Image", Category.IMAGE),
    IMAGE_PNG("image/png", "PNG Image", Category.IMAGE),
    IMAGE_GIF("image/gif", "GIF Image", Category.IMAGE),
    IMAGE_BMP("image/bmp", "Bitmap Image", Category.IMAGE),
    IMAGE_WEBP("image/webp", "WebP Image", Category.IMAGE),

    // Audio File Types
    AUDIO_MPEG("audio/mpeg", "MP3 Audio", Category.AUDIO),
    AUDIO_WAV("audio/wav", "WAV Audio", Category.AUDIO),
    AUDIO_OGG("audio/ogg", "OGG Audio", Category.AUDIO),
    AUDIO_FLAC("audio/flac", "FLAC Audio", Category.AUDIO),

    // Video File Types
    VIDEO_MP4("video/mp4", "MP4 Video", Category.VIDEO),
    VIDEO_X_MATROSKA("video/x-matroska", "MKV Video", Category.VIDEO),
    VIDEO_WEBM("video/webm", "WebM Video", Category.VIDEO),
    VIDEO_3GPP("video/3gpp", "3GPP Video", Category.VIDEO),

    // Document File Types
    APPLICATION_PDF("application/pdf", "PDF Document", Category.DOCUMENT),
    APPLICATION_MSWORD("application/msword", "Microsoft Word Document", Category.DOCUMENT),
    APPLICATION_VND_OPENXMLFORMATS_DOCUMENT("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "Microsoft Word Document (DOCX)", Category.DOCUMENT),
    APPLICATION_VND_MS_EXCEL("application/vnd.ms-excel", "Microsoft Excel Spreadsheet", Category.DOCUMENT),
    APPLICATION_VND_OPENXMLFORMATS_SHEET("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "Microsoft Excel Spreadsheet (XLSX)", Category.DOCUMENT),
    APPLICATION_VND_MS_POWERPOINT("application/vnd.ms-powerpoint", "Microsoft PowerPoint Presentation", Category.DOCUMENT),
    APPLICATION_VND_OPENXMLFORMATS_PRESENTATION("application/vnd.openxmlformats-officedocument.presentationml.presentation", "Microsoft PowerPoint Presentation (PPTX)", Category.DOCUMENT),

    // Archive File Types
    APPLICATION_ZIP("application/zip", "ZIP Archive", Category.ARCHIVE),
    APPLICATION_X_TAR("application/x-tar", "TAR Archive", Category.ARCHIVE),
    APPLICATION_GZIP("application/gzip", "GZIP Archive", Category.ARCHIVE),
    APPLICATION_X_BZIP2("application/x-bzip2", "BZIP2 Archive", Category.ARCHIVE),
    APPLICATION_X_RAR_COMPRESSED("application/x-rar-compressed", "RAR Archive", Category.ARCHIVE),

    // Font File Types
    APPLICATION_X_FONT_TTF("application/x-font-ttf", "TrueType Font", Category.FONT),
    APPLICATION_X_FONT_OTF("application/x-font-otf", "OpenType Font", Category.FONT),

    // JSON and XML File Types
    APPLICATION_JSON("application/json", "JSON File", Category.DOCUMENT),
    APPLICATION_XML("application/xml", "XML File", Category.DOCUMENT),

    // Other Common File Types
    APPLICATION_PGP_SIGNATURE("application/pgp-signature", "PGP Signature", Category.OTHER),
    APPLICATION_X_7Z_COMPRESSION("application/x-7z-compressed", "7z Archive", Category.ARCHIVE),
    SVG("image/svg+xml", "SVG Image", Category.IMAGE),
    EPS("application/postscript", "EPS File", Category.IMAGE),
    PDF_VECTOR("application/pdf", "PDF (Vector)", Category.DOCUMENT),
    AI("application/postscript", "Adobe Illustrator File", Category.IMAGE),
    WMF("application/x-msmetafile", "Windows Metafile", Category.IMAGE),
    EMF("application/emf", "Enhanced Metafile", Category.IMAGE),
    DXF("image/vnd.dxf", "DXF File", Category.IMAGE),
    XPS("application/vnd.ms-xpsdocument", "XPS Document", Category.DOCUMENT),
    CFF("application/font-sfnt", "Compact Font Format", Category.FONT),
    PS("application/postscript", "PostScript File", Category.DOCUMENT)
    //
    ;

    // Fields for the MIME type, description, and category
    private final String mimeType;
    private final String description;
    private final Category category;

    // Constructor
    MimeType(String mimeType, String description, Category category) {
        this.mimeType = mimeType;
        this.description = description;
        this.category = category;
    }

    public static Optional<MimeType> ofContentType(String contentType) {
        return Arrays.stream(values()).filter(e -> e.mimeType.equals(contentType)).findAny();
    }

    public static List<MimeType> getByCategory(Category category) {
        List<MimeType> mimeTypes = new ArrayList<>();
        for (MimeType type : MimeType.values()) {
            if (type.getCategory() == category) {
                mimeTypes.add(type);
            }
        }
        return mimeTypes;
    }

    // Inner enum for categories
    public enum Category {
        TEXT("Text Files"),
        IMAGE("Image Files"),
        AUDIO("Audio Files"),
        VIDEO("Video Files"),
        DOCUMENT("Document Files"),
        ARCHIVE("Archive Files"),
        FONT("Font Files"),
        OTHER("Other Files");

        private final String description;

        Category(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
