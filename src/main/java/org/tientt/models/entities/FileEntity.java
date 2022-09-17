package org.tientt.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.tientt.services.mappers.FileTypeConverter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "file", schema = "dbo")
public class FileEntity {
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "created_at", nullable = false)
    private long createdAt;

    @Column(name = "updated_at", nullable = false)
    private long updatedAt;

    @Column(name = "type")
    @Convert(converter = FileTypeConverter.class)
    private FileType type;

    @Column(name = "content")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private FileEntity parent;

    @OneToMany
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private List<FileEntity> children;

    public long getSize() {
        if (this.children == null) return 0;
        return this.children.stream().map(FileEntity::getSize).mapToLong(Long::longValue).sum();
    }
}
