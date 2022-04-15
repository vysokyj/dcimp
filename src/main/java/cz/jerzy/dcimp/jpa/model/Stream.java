package cz.jerzy.dcimp.jpa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(indexes = {
        @Index(columnList = "checksum"),
        @Index(columnList = "dateCreated"),
        @Index(columnList = "fileName"),
        @Index(columnList = "fileSize"),
        @Index(columnList = "originalFileName"),
        @Index(columnList = "length")
})
public class Stream implements Media {

    @Id
    @GeneratedValue
    private Long id;

    private String checksum;

    private String originalFileName;

    private Date dateCreated;

    private Date dateImported;

    private String fileName;

    private Long fileSize;

    private Long length;

}
