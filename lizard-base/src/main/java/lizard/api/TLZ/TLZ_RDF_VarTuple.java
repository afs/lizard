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
public class TLZ_RDF_VarTuple implements org.apache.thrift.TBase<TLZ_RDF_VarTuple, TLZ_RDF_VarTuple._Fields>, java.io.Serializable, Cloneable, Comparable<TLZ_RDF_VarTuple> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLZ_RDF_VarTuple");

  private static final org.apache.thrift.protocol.TField VARS_FIELD_DESC = new org.apache.thrift.protocol.TField("vars", org.apache.thrift.protocol.TType.LIST, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TLZ_RDF_VarTupleStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TLZ_RDF_VarTupleTupleSchemeFactory());
  }

  public List<TLZ_RDF_VAR> vars; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    VARS((short)1, "vars");

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
        case 1: // VARS
          return VARS;
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
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.VARS, new org.apache.thrift.meta_data.FieldMetaData("vars", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_RDF_VAR.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLZ_RDF_VarTuple.class, metaDataMap);
  }

  public TLZ_RDF_VarTuple() {
  }

  public TLZ_RDF_VarTuple(
    List<TLZ_RDF_VAR> vars)
  {
    this();
    this.vars = vars;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLZ_RDF_VarTuple(TLZ_RDF_VarTuple other) {
    if (other.isSetVars()) {
      List<TLZ_RDF_VAR> __this__vars = new ArrayList<TLZ_RDF_VAR>(other.vars.size());
      for (TLZ_RDF_VAR other_element : other.vars) {
        __this__vars.add(new TLZ_RDF_VAR(other_element));
      }
      this.vars = __this__vars;
    }
  }

  public TLZ_RDF_VarTuple deepCopy() {
    return new TLZ_RDF_VarTuple(this);
  }

  @Override
  public void clear() {
    this.vars = null;
  }

  public int getVarsSize() {
    return (this.vars == null) ? 0 : this.vars.size();
  }

  public java.util.Iterator<TLZ_RDF_VAR> getVarsIterator() {
    return (this.vars == null) ? null : this.vars.iterator();
  }

  public void addToVars(TLZ_RDF_VAR elem) {
    if (this.vars == null) {
      this.vars = new ArrayList<TLZ_RDF_VAR>();
    }
    this.vars.add(elem);
  }

  public List<TLZ_RDF_VAR> getVars() {
    return this.vars;
  }

  public TLZ_RDF_VarTuple setVars(List<TLZ_RDF_VAR> vars) {
    this.vars = vars;
    return this;
  }

  public void unsetVars() {
    this.vars = null;
  }

  /** Returns true if field vars is set (has been assigned a value) and false otherwise */
  public boolean isSetVars() {
    return this.vars != null;
  }

  public void setVarsIsSet(boolean value) {
    if (!value) {
      this.vars = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case VARS:
      if (value == null) {
        unsetVars();
      } else {
        setVars((List<TLZ_RDF_VAR>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case VARS:
      return getVars();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case VARS:
      return isSetVars();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TLZ_RDF_VarTuple)
      return this.equals((TLZ_RDF_VarTuple)that);
    return false;
  }

  public boolean equals(TLZ_RDF_VarTuple that) {
    if (that == null)
      return false;

    boolean this_present_vars = true && this.isSetVars();
    boolean that_present_vars = true && that.isSetVars();
    if (this_present_vars || that_present_vars) {
      if (!(this_present_vars && that_present_vars))
        return false;
      if (!this.vars.equals(that.vars))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TLZ_RDF_VarTuple other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetVars()).compareTo(other.isSetVars());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetVars()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.vars, other.vars);
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
    StringBuilder sb = new StringBuilder("TLZ_RDF_VarTuple(");
    boolean first = true;

    sb.append("vars:");
    if (this.vars == null) {
      sb.append("null");
    } else {
      sb.append(this.vars);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
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
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TLZ_RDF_VarTupleStandardSchemeFactory implements SchemeFactory {
    public TLZ_RDF_VarTupleStandardScheme getScheme() {
      return new TLZ_RDF_VarTupleStandardScheme();
    }
  }

  private static class TLZ_RDF_VarTupleStandardScheme extends StandardScheme<TLZ_RDF_VarTuple> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLZ_RDF_VarTuple struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // VARS
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list16 = iprot.readListBegin();
                struct.vars = new ArrayList<TLZ_RDF_VAR>(_list16.size);
                for (int _i17 = 0; _i17 < _list16.size; ++_i17)
                {
                  TLZ_RDF_VAR _elem18;
                  _elem18 = new TLZ_RDF_VAR();
                  _elem18.read(iprot);
                  struct.vars.add(_elem18);
                }
                iprot.readListEnd();
              }
              struct.setVarsIsSet(true);
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
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLZ_RDF_VarTuple struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.vars != null) {
        oprot.writeFieldBegin(VARS_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.vars.size()));
          for (TLZ_RDF_VAR _iter19 : struct.vars)
          {
            _iter19.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TLZ_RDF_VarTupleTupleSchemeFactory implements SchemeFactory {
    public TLZ_RDF_VarTupleTupleScheme getScheme() {
      return new TLZ_RDF_VarTupleTupleScheme();
    }
  }

  private static class TLZ_RDF_VarTupleTupleScheme extends TupleScheme<TLZ_RDF_VarTuple> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLZ_RDF_VarTuple struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      BitSet optionals = new BitSet();
      if (struct.isSetVars()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetVars()) {
        {
          oprot.writeI32(struct.vars.size());
          for (TLZ_RDF_VAR _iter20 : struct.vars)
          {
            _iter20.write(oprot);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLZ_RDF_VarTuple struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list21 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.vars = new ArrayList<TLZ_RDF_VAR>(_list21.size);
          for (int _i22 = 0; _i22 < _list21.size; ++_i22)
          {
            TLZ_RDF_VAR _elem23;
            _elem23 = new TLZ_RDF_VAR();
            _elem23.read(iprot);
            struct.vars.add(_elem23);
          }
        }
        struct.setVarsIsSet(true);
      }
    }
  }

}
