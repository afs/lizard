/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package lizard.api.TLZ;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("all")
public class TLZ_TupleNodeId implements org.apache.thrift.TBase<TLZ_TupleNodeId, TLZ_TupleNodeId._Fields>, java.io.Serializable, Cloneable, Comparable<TLZ_TupleNodeId> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLZ_TupleNodeId");

  private static final org.apache.thrift.protocol.TField S_FIELD_DESC = new org.apache.thrift.protocol.TField("S", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField P_FIELD_DESC = new org.apache.thrift.protocol.TField("P", org.apache.thrift.protocol.TType.I64, (short)2);
  private static final org.apache.thrift.protocol.TField O_FIELD_DESC = new org.apache.thrift.protocol.TField("O", org.apache.thrift.protocol.TType.I64, (short)3);
  private static final org.apache.thrift.protocol.TField G_FIELD_DESC = new org.apache.thrift.protocol.TField("G", org.apache.thrift.protocol.TType.I64, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TLZ_TupleNodeIdStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TLZ_TupleNodeIdTupleSchemeFactory());
  }

  public long S; // required
  public long P; // required
  public long O; // required
  public long G; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    S((short)1, "S"),
    P((short)2, "P"),
    O((short)3, "O"),
    G((short)4, "G");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // S
          return S;
        case 2: // P
          return P;
        case 3: // O
          return O;
        case 4: // G
          return G;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __S_ISSET_ID = 0;
  private static final int __P_ISSET_ID = 1;
  private static final int __O_ISSET_ID = 2;
  private static final int __G_ISSET_ID = 3;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.G};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.S, new org.apache.thrift.meta_data.FieldMetaData("S", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.P, new org.apache.thrift.meta_data.FieldMetaData("P", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.O, new org.apache.thrift.meta_data.FieldMetaData("O", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.G, new org.apache.thrift.meta_data.FieldMetaData("G", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLZ_TupleNodeId.class, metaDataMap);
  }

  public TLZ_TupleNodeId() {
  }

  public TLZ_TupleNodeId(
    long S,
    long P,
    long O)
  {
    this();
    this.S = S;
    setSIsSet(true);
    this.P = P;
    setPIsSet(true);
    this.O = O;
    setOIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLZ_TupleNodeId(TLZ_TupleNodeId other) {
    __isset_bitfield = other.__isset_bitfield;
    this.S = other.S;
    this.P = other.P;
    this.O = other.O;
    this.G = other.G;
  }

  public TLZ_TupleNodeId deepCopy() {
    return new TLZ_TupleNodeId(this);
  }

  @Override
  public void clear() {
    setSIsSet(false);
    this.S = 0;
    setPIsSet(false);
    this.P = 0;
    setOIsSet(false);
    this.O = 0;
    setGIsSet(false);
    this.G = 0;
  }

  public long getS() {
    return this.S;
  }

  public TLZ_TupleNodeId setS(long S) {
    this.S = S;
    setSIsSet(true);
    return this;
  }

  public void unsetS() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __S_ISSET_ID);
  }

  /** Returns true if field S is set (has been assigned a value) and false otherwise */
  public boolean isSetS() {
    return EncodingUtils.testBit(__isset_bitfield, __S_ISSET_ID);
  }

  public void setSIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __S_ISSET_ID, value);
  }

  public long getP() {
    return this.P;
  }

  public TLZ_TupleNodeId setP(long P) {
    this.P = P;
    setPIsSet(true);
    return this;
  }

  public void unsetP() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __P_ISSET_ID);
  }

  /** Returns true if field P is set (has been assigned a value) and false otherwise */
  public boolean isSetP() {
    return EncodingUtils.testBit(__isset_bitfield, __P_ISSET_ID);
  }

  public void setPIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __P_ISSET_ID, value);
  }

  public long getO() {
    return this.O;
  }

  public TLZ_TupleNodeId setO(long O) {
    this.O = O;
    setOIsSet(true);
    return this;
  }

  public void unsetO() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __O_ISSET_ID);
  }

  /** Returns true if field O is set (has been assigned a value) and false otherwise */
  public boolean isSetO() {
    return EncodingUtils.testBit(__isset_bitfield, __O_ISSET_ID);
  }

  public void setOIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __O_ISSET_ID, value);
  }

  public long getG() {
    return this.G;
  }

  public TLZ_TupleNodeId setG(long G) {
    this.G = G;
    setGIsSet(true);
    return this;
  }

  public void unsetG() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __G_ISSET_ID);
  }

  /** Returns true if field G is set (has been assigned a value) and false otherwise */
  public boolean isSetG() {
    return EncodingUtils.testBit(__isset_bitfield, __G_ISSET_ID);
  }

  public void setGIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __G_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case S:
      if (value == null) {
        unsetS();
      } else {
        setS((Long)value);
      }
      break;

    case P:
      if (value == null) {
        unsetP();
      } else {
        setP((Long)value);
      }
      break;

    case O:
      if (value == null) {
        unsetO();
      } else {
        setO((Long)value);
      }
      break;

    case G:
      if (value == null) {
        unsetG();
      } else {
        setG((Long)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case S:
      return Long.valueOf(getS());

    case P:
      return Long.valueOf(getP());

    case O:
      return Long.valueOf(getO());

    case G:
      return Long.valueOf(getG());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case S:
      return isSetS();
    case P:
      return isSetP();
    case O:
      return isSetO();
    case G:
      return isSetG();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TLZ_TupleNodeId)
      return this.equals((TLZ_TupleNodeId)that);
    return false;
  }

  public boolean equals(TLZ_TupleNodeId that) {
    if (that == null)
      return false;

    boolean this_present_S = true;
    boolean that_present_S = true;
    if (this_present_S || that_present_S) {
      if (!(this_present_S && that_present_S))
        return false;
      if (this.S != that.S)
        return false;
    }

    boolean this_present_P = true;
    boolean that_present_P = true;
    if (this_present_P || that_present_P) {
      if (!(this_present_P && that_present_P))
        return false;
      if (this.P != that.P)
        return false;
    }

    boolean this_present_O = true;
    boolean that_present_O = true;
    if (this_present_O || that_present_O) {
      if (!(this_present_O && that_present_O))
        return false;
      if (this.O != that.O)
        return false;
    }

    boolean this_present_G = true && this.isSetG();
    boolean that_present_G = true && that.isSetG();
    if (this_present_G || that_present_G) {
      if (!(this_present_G && that_present_G))
        return false;
      if (this.G != that.G)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TLZ_TupleNodeId other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetS()).compareTo(other.isSetS());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetS()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.S, other.S);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetP()).compareTo(other.isSetP());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetP()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.P, other.P);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetO()).compareTo(other.isSetO());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetO()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.O, other.O);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetG()).compareTo(other.isSetG());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetG()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.G, other.G);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TLZ_TupleNodeId(");
    boolean first = true;

    sb.append("S:");
    sb.append(this.S);
    first = false;
    if (!first) sb.append(", ");
    sb.append("P:");
    sb.append(this.P);
    first = false;
    if (!first) sb.append(", ");
    sb.append("O:");
    sb.append(this.O);
    first = false;
    if (isSetG()) {
      if (!first) sb.append(", ");
      sb.append("G:");
      sb.append(this.G);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'S' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'P' because it's a primitive and you chose the non-beans generator.
    // alas, we cannot check 'O' because it's a primitive and you chose the non-beans generator.
    // check for sub-struct validity
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TLZ_TupleNodeIdStandardSchemeFactory implements SchemeFactory {
    public TLZ_TupleNodeIdStandardScheme getScheme() {
      return new TLZ_TupleNodeIdStandardScheme();
    }
  }

  private static class TLZ_TupleNodeIdStandardScheme extends StandardScheme<TLZ_TupleNodeId> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLZ_TupleNodeId struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // S
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.S = iprot.readI64();
              struct.setSIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // P
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.P = iprot.readI64();
              struct.setPIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // O
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.O = iprot.readI64();
              struct.setOIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // G
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.G = iprot.readI64();
              struct.setGIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      if (!struct.isSetS()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'S' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetP()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'P' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetO()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'O' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLZ_TupleNodeId struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(S_FIELD_DESC);
      oprot.writeI64(struct.S);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(P_FIELD_DESC);
      oprot.writeI64(struct.P);
      oprot.writeFieldEnd();
      oprot.writeFieldBegin(O_FIELD_DESC);
      oprot.writeI64(struct.O);
      oprot.writeFieldEnd();
      if (struct.isSetG()) {
        oprot.writeFieldBegin(G_FIELD_DESC);
        oprot.writeI64(struct.G);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TLZ_TupleNodeIdTupleSchemeFactory implements SchemeFactory {
    public TLZ_TupleNodeIdTupleScheme getScheme() {
      return new TLZ_TupleNodeIdTupleScheme();
    }
  }

  private static class TLZ_TupleNodeIdTupleScheme extends TupleScheme<TLZ_TupleNodeId> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLZ_TupleNodeId struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI64(struct.S);
      oprot.writeI64(struct.P);
      oprot.writeI64(struct.O);
      BitSet optionals = new BitSet();
      if (struct.isSetG()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetG()) {
        oprot.writeI64(struct.G);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLZ_TupleNodeId struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.S = iprot.readI64();
      struct.setSIsSet(true);
      struct.P = iprot.readI64();
      struct.setPIsSet(true);
      struct.O = iprot.readI64();
      struct.setOIsSet(true);
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        struct.G = iprot.readI64();
        struct.setGIsSet(true);
      }
    }
  }

}

