package com.synaway.oneplaces.model;

import java.io.IOException;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializableWithType;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

@Entity
@Table(name = "spot")
@Inheritance(strategy = InheritanceType.JOINED)
public class Spot implements JsonSerializableWithType {

    @Id
    @GeneratedValue(generator = "spot_id", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "spot_id", sequenceName = "spot_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "status")
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "location", columnDefinition = "Geometry")
    @Type(type = "org.hibernate.spatial.GeometryType")
    private Point location;

    @Column(name = "created_at")
    private Date timestamp;

    @Column(name = "flag")
    private String flag;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "Spot [id=" + id + ", status=" + status + ", user=" + user + ", location=" + location + "]";
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void serialize(JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();
        if (id == null) {
            jgen.writeNullField("spotId");
        } else {
            jgen.writeNumberField("spotId", id);
        }

        if (timestamp == null) {
            jgen.writeNullField("timestamp");
        } else {
            jgen.writeNumberField("timestamp", timestamp.getTime() / 1000);
        }

        if (location == null) {
            jgen.writeNullField("longitude");
        } else {
            jgen.writeNumberField("longitude", location.getX());
        }

        if (location == null) {
            jgen.writeNullField("latitude");
        } else {
            jgen.writeNumberField("latitude", location.getY());
        }

        if (status == null) {
            jgen.writeNullField("status");
        } else {
            jgen.writeStringField("status", status);
        }

        if (flag == null) {
            jgen.writeNullField("flag");
        } else {
            jgen.writeStringField("flag", flag);
        }

        jgen.writeEndObject();
    }

    @Override
    public void serializeWithType(JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) {
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }
}
