package org.tientt.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.tientt.services.mappers.FileTypeConverter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    @Column(name = "name", nullable = false)
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

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    private List<FileEntity> children;

    public long getSize() {
        if (this.type == FileType.TEXT_FILE) {
            if (content == null) return 0;
            return content.length();
        };
        if (this.children == null) return 0;
        return this.children.stream().map(FileEntity::getSize).mapToLong(Long::longValue).sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        FileEntity file = (FileEntity) o;
        return Objects.equals(id, file.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
