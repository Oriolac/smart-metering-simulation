package udl.cig.sms.connection.datagram;

import cat.udl.cig.fields.GroupElement;
import udl.cig.sms.connection.Datagrams;

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
        byte[] c = element.toBytes();
        byte[] res = new byte[c.length + 1];
        res[0] = (byte) Datagrams.GROUP_ELEMENT_DATAGRAM.ordinal();
        System.arraycopy(c, 0, res, 1, c.length);
        return res;
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
