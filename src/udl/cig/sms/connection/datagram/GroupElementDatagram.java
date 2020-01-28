package udl.cig.sms.connection.datagram;

import cat.udl.cig.fields.GroupElement;

import java.util.Objects;

public class GroupElementDatagram implements SMSDatagram {

    private final GroupElement element;

    public GroupElementDatagram(GroupElement element) {
        this.element = element;
    }

    public GroupElement getElement() {
        return element;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupElementDatagram that = (GroupElementDatagram) o;
        return Objects.equals(element, that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }

    @Override
    public String toString() {
        return "GroupElementDatagram{" +
                "element=" + element +
                '}';
    }
}
