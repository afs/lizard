/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  See the NOTICE file distributed with this work for additional
 *  information regarding copyright ownership.
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
public class TLZ_SubjectPredicateList implements org.apache.thrift.TBase<TLZ_SubjectPredicateList, TLZ_SubjectPredicateList._Fields>, java.io.Serializable, Cloneable, Comparable<TLZ_SubjectPredicateList> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLZ_SubjectPredicateList");

  private static final org.apache.thrift.protocol.TField SUBJECT_FIELD_DESC = new org.apache.thrift.protocol.TField("subject", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField PREDICATES_FIELD_DESC = new org.apache.thrift.protocol.TField("predicates", org.apache.thrift.protocol.TType.LIST, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TLZ_SubjectPredicateListStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TLZ_SubjectPredicateListTupleSchemeFactory());
  }

  public long subject; // required
  public List<Long> predicates; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    SUBJECT((short)1, "subject"),
    PREDICATES((short)2, "predicates");

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
        case 1: // SUBJECT
          return SUBJECT;
        case 2: // PREDICATES
          return PREDICATES;
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
  private static final int __SUBJECT_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.SUBJECT, new org.apache.thrift.meta_data.FieldMetaData("subject", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.PREDICATES, new org.apache.thrift.meta_data.FieldMetaData("predicates", org.apache.thrift.TFieldRequirementType.DEFAULT, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLZ_SubjectPredicateList.class, metaDataMap);
  }

  public TLZ_SubjectPredicateList() {
  }

  public TLZ_SubjectPredicateList(
    long subject,
    List<Long> predicates)
  {
    this();
    this.subject = subject;
    setSubjectIsSet(true);
    this.predicates = predicates;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLZ_SubjectPredicateList(TLZ_SubjectPredicateList other) {
    __isset_bitfield = other.__isset_bitfield;
    this.subject = other.subject;
    if (other.isSetPredicates()) {
      List<Long> __this__predicates = new ArrayList<Long>(other.predicates);
      this.predicates = __this__predicates;
    }
  }

  public TLZ_SubjectPredicateList deepCopy() {
    return new TLZ_SubjectPredicateList(this);
  }

  @Override
  public void clear() {
    setSubjectIsSet(false);
    this.subject = 0;
    this.predicates = null;
  }

  public long getSubject() {
    return this.subject;
  }

  public TLZ_SubjectPredicateList setSubject(long subject) {
    this.subject = subject;
    setSubjectIsSet(true);
    return this;
  }

  public void unsetSubject() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __SUBJECT_ISSET_ID);
  }

  /** Returns true if field subject is set (has been assigned a value) and false otherwise */
  public boolean isSetSubject() {
    return EncodingUtils.testBit(__isset_bitfield, __SUBJECT_ISSET_ID);
  }

  public void setSubjectIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __SUBJECT_ISSET_ID, value);
  }

  public int getPredicatesSize() {
    return (this.predicates == null) ? 0 : this.predicates.size();
  }

  public java.util.Iterator<Long> getPredicatesIterator() {
    return (this.predicates == null) ? null : this.predicates.iterator();
  }

  public void addToPredicates(long elem) {
    if (this.predicates == null) {
      this.predicates = new ArrayList<Long>();
    }
    this.predicates.add(elem);
  }

  public List<Long> getPredicates() {
    return this.predicates;
  }

  public TLZ_SubjectPredicateList setPredicates(List<Long> predicates) {
    this.predicates = predicates;
    return this;
  }

  public void unsetPredicates() {
    this.predicates = null;
  }

  /** Returns true if field predicates is set (has been assigned a value) and false otherwise */
  public boolean isSetPredicates() {
    return this.predicates != null;
  }

  public void setPredicatesIsSet(boolean value) {
    if (!value) {
      this.predicates = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case SUBJECT:
      if (value == null) {
        unsetSubject();
      } else {
        setSubject((Long)value);
      }
      break;

    case PREDICATES:
      if (value == null) {
        unsetPredicates();
      } else {
        setPredicates((List<Long>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case SUBJECT:
      return Long.valueOf(getSubject());

    case PREDICATES:
      return getPredicates();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case SUBJECT:
      return isSetSubject();
    case PREDICATES:
      return isSetPredicates();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TLZ_SubjectPredicateList)
      return this.equals((TLZ_SubjectPredicateList)that);
    return false;
  }

  public boolean equals(TLZ_SubjectPredicateList that) {
    if (that == null)
      return false;

    boolean this_present_subject = true;
    boolean that_present_subject = true;
    if (this_present_subject || that_present_subject) {
      if (!(this_present_subject && that_present_subject))
        return false;
      if (this.subject != that.subject)
        return false;
    }

    boolean this_present_predicates = true && this.isSetPredicates();
    boolean that_present_predicates = true && that.isSetPredicates();
    if (this_present_predicates || that_present_predicates) {
      if (!(this_present_predicates && that_present_predicates))
        return false;
      if (!this.predicates.equals(that.predicates))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TLZ_SubjectPredicateList other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetSubject()).compareTo(other.isSetSubject());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSubject()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.subject, other.subject);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPredicates()).compareTo(other.isSetPredicates());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPredicates()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.predicates, other.predicates);
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
    StringBuilder sb = new StringBuilder("TLZ_SubjectPredicateList(");
    boolean first = true;

    sb.append("subject:");
    sb.append(this.subject);
    first = false;
    if (!first) sb.append(", ");
    sb.append("predicates:");
    if (this.predicates == null) {
      sb.append("null");
    } else {
      sb.append(this.predicates);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'subject' because it's a primitive and you chose the non-beans generator.
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

  private static class TLZ_SubjectPredicateListStandardSchemeFactory implements SchemeFactory {
    public TLZ_SubjectPredicateListStandardScheme getScheme() {
      return new TLZ_SubjectPredicateListStandardScheme();
    }
  }

  private static class TLZ_SubjectPredicateListStandardScheme extends StandardScheme<TLZ_SubjectPredicateList> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLZ_SubjectPredicateList struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // SUBJECT
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.subject = iprot.readI64();
              struct.setSubjectIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PREDICATES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.predicates = new ArrayList<Long>(_list0.size);
                for (int _i1 = 0; _i1 < _list0.size; ++_i1)
                {
                  long _elem2;
                  _elem2 = iprot.readI64();
                  struct.predicates.add(_elem2);
                }
                iprot.readListEnd();
              }
              struct.setPredicatesIsSet(true);
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
      if (!struct.isSetSubject()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'subject' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLZ_SubjectPredicateList struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(SUBJECT_FIELD_DESC);
      oprot.writeI64(struct.subject);
      oprot.writeFieldEnd();
      if (struct.predicates != null) {
        oprot.writeFieldBegin(PREDICATES_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I64, struct.predicates.size()));
          for (long _iter3 : struct.predicates)
          {
            oprot.writeI64(_iter3);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TLZ_SubjectPredicateListTupleSchemeFactory implements SchemeFactory {
    public TLZ_SubjectPredicateListTupleScheme getScheme() {
      return new TLZ_SubjectPredicateListTupleScheme();
    }
  }

  private static class TLZ_SubjectPredicateListTupleScheme extends TupleScheme<TLZ_SubjectPredicateList> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLZ_SubjectPredicateList struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI64(struct.subject);
      BitSet optionals = new BitSet();
      if (struct.isSetPredicates()) {
        optionals.set(0);
      }
      oprot.writeBitSet(optionals, 1);
      if (struct.isSetPredicates()) {
        {
          oprot.writeI32(struct.predicates.size());
          for (long _iter4 : struct.predicates)
          {
            oprot.writeI64(_iter4);
          }
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLZ_SubjectPredicateList struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.subject = iprot.readI64();
      struct.setSubjectIsSet(true);
      BitSet incoming = iprot.readBitSet(1);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.I64, iprot.readI32());
          struct.predicates = new ArrayList<Long>(_list5.size);
          for (int _i6 = 0; _i6 < _list5.size; ++_i6)
          {
            long _elem7;
            _elem7 = iprot.readI64();
            struct.predicates.add(_elem7);
          }
        }
        struct.setPredicatesIsSet(true);
      }
    }
  }

}

