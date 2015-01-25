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
public class TLZ_IdxReply implements org.apache.thrift.TBase<TLZ_IdxReply, TLZ_IdxReply._Fields>, java.io.Serializable, Cloneable, Comparable<TLZ_IdxReply> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLZ_IdxReply");

  private static final org.apache.thrift.protocol.TField REQUEST_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("requestId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField ENTITIES_FIELD_DESC = new org.apache.thrift.protocol.TField("entities", org.apache.thrift.protocol.TType.LIST, (short)2);
  private static final org.apache.thrift.protocol.TField TUPLES_FIELD_DESC = new org.apache.thrift.protocol.TField("tuples", org.apache.thrift.protocol.TType.LIST, (short)3);
  private static final org.apache.thrift.protocol.TField YES_OR_NO_FIELD_DESC = new org.apache.thrift.protocol.TField("yesOrNo", org.apache.thrift.protocol.TType.BOOL, (short)4);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TLZ_IdxReplyStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TLZ_IdxReplyTupleSchemeFactory());
  }

  public long requestId; // required
  public List<TLZ_SubjectPredicateObjectList> entities; // optional
  public List<TLZ_TupleNodeId> tuples; // optional
  public boolean yesOrNo; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    REQUEST_ID((short)1, "requestId"),
    ENTITIES((short)2, "entities"),
    TUPLES((short)3, "tuples"),
    YES_OR_NO((short)4, "yesOrNo");

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
        case 1: // REQUEST_ID
          return REQUEST_ID;
        case 2: // ENTITIES
          return ENTITIES;
        case 3: // TUPLES
          return TUPLES;
        case 4: // YES_OR_NO
          return YES_OR_NO;
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
  private static final int __REQUESTID_ISSET_ID = 0;
  private static final int __YESORNO_ISSET_ID = 1;
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.ENTITIES,_Fields.TUPLES,_Fields.YES_OR_NO};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.REQUEST_ID, new org.apache.thrift.meta_data.FieldMetaData("requestId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.ENTITIES, new org.apache.thrift.meta_data.FieldMetaData("entities", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_SubjectPredicateObjectList.class))));
    tmpMap.put(_Fields.TUPLES, new org.apache.thrift.meta_data.FieldMetaData("tuples", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_TupleNodeId.class))));
    tmpMap.put(_Fields.YES_OR_NO, new org.apache.thrift.meta_data.FieldMetaData("yesOrNo", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.BOOL)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLZ_IdxReply.class, metaDataMap);
  }

  public TLZ_IdxReply() {
  }

  public TLZ_IdxReply(
    long requestId)
  {
    this();
    this.requestId = requestId;
    setRequestIdIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLZ_IdxReply(TLZ_IdxReply other) {
    __isset_bitfield = other.__isset_bitfield;
    this.requestId = other.requestId;
    if (other.isSetEntities()) {
      List<TLZ_SubjectPredicateObjectList> __this__entities = new ArrayList<TLZ_SubjectPredicateObjectList>(other.entities.size());
      for (TLZ_SubjectPredicateObjectList other_element : other.entities) {
        __this__entities.add(new TLZ_SubjectPredicateObjectList(other_element));
      }
      this.entities = __this__entities;
    }
    if (other.isSetTuples()) {
      List<TLZ_TupleNodeId> __this__tuples = new ArrayList<TLZ_TupleNodeId>(other.tuples.size());
      for (TLZ_TupleNodeId other_element : other.tuples) {
        __this__tuples.add(new TLZ_TupleNodeId(other_element));
      }
      this.tuples = __this__tuples;
    }
    this.yesOrNo = other.yesOrNo;
  }

  public TLZ_IdxReply deepCopy() {
    return new TLZ_IdxReply(this);
  }

  @Override
  public void clear() {
    setRequestIdIsSet(false);
    this.requestId = 0;
    this.entities = null;
    this.tuples = null;
    setYesOrNoIsSet(false);
    this.yesOrNo = false;
  }

  public long getRequestId() {
    return this.requestId;
  }

  public TLZ_IdxReply setRequestId(long requestId) {
    this.requestId = requestId;
    setRequestIdIsSet(true);
    return this;
  }

  public void unsetRequestId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __REQUESTID_ISSET_ID);
  }

  /** Returns true if field requestId is set (has been assigned a value) and false otherwise */
  public boolean isSetRequestId() {
    return EncodingUtils.testBit(__isset_bitfield, __REQUESTID_ISSET_ID);
  }

  public void setRequestIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __REQUESTID_ISSET_ID, value);
  }

  public int getEntitiesSize() {
    return (this.entities == null) ? 0 : this.entities.size();
  }

  public java.util.Iterator<TLZ_SubjectPredicateObjectList> getEntitiesIterator() {
    return (this.entities == null) ? null : this.entities.iterator();
  }

  public void addToEntities(TLZ_SubjectPredicateObjectList elem) {
    if (this.entities == null) {
      this.entities = new ArrayList<TLZ_SubjectPredicateObjectList>();
    }
    this.entities.add(elem);
  }

  public List<TLZ_SubjectPredicateObjectList> getEntities() {
    return this.entities;
  }

  public TLZ_IdxReply setEntities(List<TLZ_SubjectPredicateObjectList> entities) {
    this.entities = entities;
    return this;
  }

  public void unsetEntities() {
    this.entities = null;
  }

  /** Returns true if field entities is set (has been assigned a value) and false otherwise */
  public boolean isSetEntities() {
    return this.entities != null;
  }

  public void setEntitiesIsSet(boolean value) {
    if (!value) {
      this.entities = null;
    }
  }

  public int getTuplesSize() {
    return (this.tuples == null) ? 0 : this.tuples.size();
  }

  public java.util.Iterator<TLZ_TupleNodeId> getTuplesIterator() {
    return (this.tuples == null) ? null : this.tuples.iterator();
  }

  public void addToTuples(TLZ_TupleNodeId elem) {
    if (this.tuples == null) {
      this.tuples = new ArrayList<TLZ_TupleNodeId>();
    }
    this.tuples.add(elem);
  }

  public List<TLZ_TupleNodeId> getTuples() {
    return this.tuples;
  }

  public TLZ_IdxReply setTuples(List<TLZ_TupleNodeId> tuples) {
    this.tuples = tuples;
    return this;
  }

  public void unsetTuples() {
    this.tuples = null;
  }

  /** Returns true if field tuples is set (has been assigned a value) and false otherwise */
  public boolean isSetTuples() {
    return this.tuples != null;
  }

  public void setTuplesIsSet(boolean value) {
    if (!value) {
      this.tuples = null;
    }
  }

  public boolean isYesOrNo() {
    return this.yesOrNo;
  }

  public TLZ_IdxReply setYesOrNo(boolean yesOrNo) {
    this.yesOrNo = yesOrNo;
    setYesOrNoIsSet(true);
    return this;
  }

  public void unsetYesOrNo() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __YESORNO_ISSET_ID);
  }

  /** Returns true if field yesOrNo is set (has been assigned a value) and false otherwise */
  public boolean isSetYesOrNo() {
    return EncodingUtils.testBit(__isset_bitfield, __YESORNO_ISSET_ID);
  }

  public void setYesOrNoIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __YESORNO_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case REQUEST_ID:
      if (value == null) {
        unsetRequestId();
      } else {
        setRequestId((Long)value);
      }
      break;

    case ENTITIES:
      if (value == null) {
        unsetEntities();
      } else {
        setEntities((List<TLZ_SubjectPredicateObjectList>)value);
      }
      break;

    case TUPLES:
      if (value == null) {
        unsetTuples();
      } else {
        setTuples((List<TLZ_TupleNodeId>)value);
      }
      break;

    case YES_OR_NO:
      if (value == null) {
        unsetYesOrNo();
      } else {
        setYesOrNo((Boolean)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case REQUEST_ID:
      return Long.valueOf(getRequestId());

    case ENTITIES:
      return getEntities();

    case TUPLES:
      return getTuples();

    case YES_OR_NO:
      return Boolean.valueOf(isYesOrNo());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case REQUEST_ID:
      return isSetRequestId();
    case ENTITIES:
      return isSetEntities();
    case TUPLES:
      return isSetTuples();
    case YES_OR_NO:
      return isSetYesOrNo();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TLZ_IdxReply)
      return this.equals((TLZ_IdxReply)that);
    return false;
  }

  public boolean equals(TLZ_IdxReply that) {
    if (that == null)
      return false;

    boolean this_present_requestId = true;
    boolean that_present_requestId = true;
    if (this_present_requestId || that_present_requestId) {
      if (!(this_present_requestId && that_present_requestId))
        return false;
      if (this.requestId != that.requestId)
        return false;
    }

    boolean this_present_entities = true && this.isSetEntities();
    boolean that_present_entities = true && that.isSetEntities();
    if (this_present_entities || that_present_entities) {
      if (!(this_present_entities && that_present_entities))
        return false;
      if (!this.entities.equals(that.entities))
        return false;
    }

    boolean this_present_tuples = true && this.isSetTuples();
    boolean that_present_tuples = true && that.isSetTuples();
    if (this_present_tuples || that_present_tuples) {
      if (!(this_present_tuples && that_present_tuples))
        return false;
      if (!this.tuples.equals(that.tuples))
        return false;
    }

    boolean this_present_yesOrNo = true && this.isSetYesOrNo();
    boolean that_present_yesOrNo = true && that.isSetYesOrNo();
    if (this_present_yesOrNo || that_present_yesOrNo) {
      if (!(this_present_yesOrNo && that_present_yesOrNo))
        return false;
      if (this.yesOrNo != that.yesOrNo)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TLZ_IdxReply other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetRequestId()).compareTo(other.isSetRequestId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetRequestId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.requestId, other.requestId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetEntities()).compareTo(other.isSetEntities());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetEntities()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.entities, other.entities);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTuples()).compareTo(other.isSetTuples());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTuples()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.tuples, other.tuples);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetYesOrNo()).compareTo(other.isSetYesOrNo());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetYesOrNo()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.yesOrNo, other.yesOrNo);
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
    StringBuilder sb = new StringBuilder("TLZ_IdxReply(");
    boolean first = true;

    sb.append("requestId:");
    sb.append(this.requestId);
    first = false;
    if (isSetEntities()) {
      if (!first) sb.append(", ");
      sb.append("entities:");
      if (this.entities == null) {
        sb.append("null");
      } else {
        sb.append(this.entities);
      }
      first = false;
    }
    if (isSetTuples()) {
      if (!first) sb.append(", ");
      sb.append("tuples:");
      if (this.tuples == null) {
        sb.append("null");
      } else {
        sb.append(this.tuples);
      }
      first = false;
    }
    if (isSetYesOrNo()) {
      if (!first) sb.append(", ");
      sb.append("yesOrNo:");
      sb.append(this.yesOrNo);
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'requestId' because it's a primitive and you chose the non-beans generator.
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

  private static class TLZ_IdxReplyStandardSchemeFactory implements SchemeFactory {
    public TLZ_IdxReplyStandardScheme getScheme() {
      return new TLZ_IdxReplyStandardScheme();
    }
  }

  private static class TLZ_IdxReplyStandardScheme extends StandardScheme<TLZ_IdxReply> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLZ_IdxReply struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // REQUEST_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.requestId = iprot.readI64();
              struct.setRequestIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // ENTITIES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list24 = iprot.readListBegin();
                struct.entities = new ArrayList<TLZ_SubjectPredicateObjectList>(_list24.size);
                for (int _i25 = 0; _i25 < _list24.size; ++_i25)
                {
                  TLZ_SubjectPredicateObjectList _elem26;
                  _elem26 = new TLZ_SubjectPredicateObjectList();
                  _elem26.read(iprot);
                  struct.entities.add(_elem26);
                }
                iprot.readListEnd();
              }
              struct.setEntitiesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // TUPLES
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list27 = iprot.readListBegin();
                struct.tuples = new ArrayList<TLZ_TupleNodeId>(_list27.size);
                for (int _i28 = 0; _i28 < _list27.size; ++_i28)
                {
                  TLZ_TupleNodeId _elem29;
                  _elem29 = new TLZ_TupleNodeId();
                  _elem29.read(iprot);
                  struct.tuples.add(_elem29);
                }
                iprot.readListEnd();
              }
              struct.setTuplesIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // YES_OR_NO
            if (schemeField.type == org.apache.thrift.protocol.TType.BOOL) {
              struct.yesOrNo = iprot.readBool();
              struct.setYesOrNoIsSet(true);
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
      if (!struct.isSetRequestId()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'requestId' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLZ_IdxReply struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(REQUEST_ID_FIELD_DESC);
      oprot.writeI64(struct.requestId);
      oprot.writeFieldEnd();
      if (struct.entities != null) {
        if (struct.isSetEntities()) {
          oprot.writeFieldBegin(ENTITIES_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.entities.size()));
            for (TLZ_SubjectPredicateObjectList _iter30 : struct.entities)
            {
              _iter30.write(oprot);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.tuples != null) {
        if (struct.isSetTuples()) {
          oprot.writeFieldBegin(TUPLES_FIELD_DESC);
          {
            oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.tuples.size()));
            for (TLZ_TupleNodeId _iter31 : struct.tuples)
            {
              _iter31.write(oprot);
            }
            oprot.writeListEnd();
          }
          oprot.writeFieldEnd();
        }
      }
      if (struct.isSetYesOrNo()) {
        oprot.writeFieldBegin(YES_OR_NO_FIELD_DESC);
        oprot.writeBool(struct.yesOrNo);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TLZ_IdxReplyTupleSchemeFactory implements SchemeFactory {
    public TLZ_IdxReplyTupleScheme getScheme() {
      return new TLZ_IdxReplyTupleScheme();
    }
  }

  private static class TLZ_IdxReplyTupleScheme extends TupleScheme<TLZ_IdxReply> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLZ_IdxReply struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI64(struct.requestId);
      BitSet optionals = new BitSet();
      if (struct.isSetEntities()) {
        optionals.set(0);
      }
      if (struct.isSetTuples()) {
        optionals.set(1);
      }
      if (struct.isSetYesOrNo()) {
        optionals.set(2);
      }
      oprot.writeBitSet(optionals, 3);
      if (struct.isSetEntities()) {
        {
          oprot.writeI32(struct.entities.size());
          for (TLZ_SubjectPredicateObjectList _iter32 : struct.entities)
          {
            _iter32.write(oprot);
          }
        }
      }
      if (struct.isSetTuples()) {
        {
          oprot.writeI32(struct.tuples.size());
          for (TLZ_TupleNodeId _iter33 : struct.tuples)
          {
            _iter33.write(oprot);
          }
        }
      }
      if (struct.isSetYesOrNo()) {
        oprot.writeBool(struct.yesOrNo);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLZ_IdxReply struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.requestId = iprot.readI64();
      struct.setRequestIdIsSet(true);
      BitSet incoming = iprot.readBitSet(3);
      if (incoming.get(0)) {
        {
          org.apache.thrift.protocol.TList _list34 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.entities = new ArrayList<TLZ_SubjectPredicateObjectList>(_list34.size);
          for (int _i35 = 0; _i35 < _list34.size; ++_i35)
          {
            TLZ_SubjectPredicateObjectList _elem36;
            _elem36 = new TLZ_SubjectPredicateObjectList();
            _elem36.read(iprot);
            struct.entities.add(_elem36);
          }
        }
        struct.setEntitiesIsSet(true);
      }
      if (incoming.get(1)) {
        {
          org.apache.thrift.protocol.TList _list37 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
          struct.tuples = new ArrayList<TLZ_TupleNodeId>(_list37.size);
          for (int _i38 = 0; _i38 < _list37.size; ++_i38)
          {
            TLZ_TupleNodeId _elem39;
            _elem39 = new TLZ_TupleNodeId();
            _elem39.read(iprot);
            struct.tuples.add(_elem39);
          }
        }
        struct.setTuplesIsSet(true);
      }
      if (incoming.get(2)) {
        struct.yesOrNo = iprot.readBool();
        struct.setYesOrNoIsSet(true);
      }
    }
  }

}

