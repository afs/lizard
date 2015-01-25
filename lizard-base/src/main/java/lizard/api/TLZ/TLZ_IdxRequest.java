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
public class TLZ_IdxRequest implements org.apache.thrift.TBase<TLZ_IdxRequest, TLZ_IdxRequest._Fields>, java.io.Serializable, Cloneable, Comparable<TLZ_IdxRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLZ_IdxRequest");

  private static final org.apache.thrift.protocol.TField REQUEST_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("requestId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField INDEX_FIELD_DESC = new org.apache.thrift.protocol.TField("index", org.apache.thrift.protocol.TType.STRUCT, (short)2);
  private static final org.apache.thrift.protocol.TField PATTERN_FIELD_DESC = new org.apache.thrift.protocol.TField("pattern", org.apache.thrift.protocol.TType.STRUCT, (short)3);
  private static final org.apache.thrift.protocol.TField SUB_PREDS_FIELD_DESC = new org.apache.thrift.protocol.TField("subPreds", org.apache.thrift.protocol.TType.STRUCT, (short)4);
  private static final org.apache.thrift.protocol.TField ADD_TUPLE_FIELD_DESC = new org.apache.thrift.protocol.TField("addTuple", org.apache.thrift.protocol.TType.STRUCT, (short)5);
  private static final org.apache.thrift.protocol.TField DELETE_TUPLE_FIELD_DESC = new org.apache.thrift.protocol.TField("deleteTuple", org.apache.thrift.protocol.TType.STRUCT, (short)6);
  private static final org.apache.thrift.protocol.TField PING_FIELD_DESC = new org.apache.thrift.protocol.TField("ping", org.apache.thrift.protocol.TType.STRUCT, (short)7);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TLZ_IdxRequestStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TLZ_IdxRequestTupleSchemeFactory());
  }

  public long requestId; // required
  public TLZ_ShardIndex index; // required
  public TLZ_TupleNodeId pattern; // optional
  public TLZ_SubjectPredicateList subPreds; // optional
  public TLZ_TupleNodeId addTuple; // optional
  public TLZ_TupleNodeId deleteTuple; // optional
  public TLZ_Ping ping; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    REQUEST_ID((short)1, "requestId"),
    INDEX((short)2, "index"),
    PATTERN((short)3, "pattern"),
    SUB_PREDS((short)4, "subPreds"),
    ADD_TUPLE((short)5, "addTuple"),
    DELETE_TUPLE((short)6, "deleteTuple"),
    PING((short)7, "ping");

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
        case 2: // INDEX
          return INDEX;
        case 3: // PATTERN
          return PATTERN;
        case 4: // SUB_PREDS
          return SUB_PREDS;
        case 5: // ADD_TUPLE
          return ADD_TUPLE;
        case 6: // DELETE_TUPLE
          return DELETE_TUPLE;
        case 7: // PING
          return PING;
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
  private byte __isset_bitfield = 0;
  private _Fields optionals[] = {_Fields.PATTERN,_Fields.SUB_PREDS,_Fields.ADD_TUPLE,_Fields.DELETE_TUPLE,_Fields.PING};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.REQUEST_ID, new org.apache.thrift.meta_data.FieldMetaData("requestId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.INDEX, new org.apache.thrift.meta_data.FieldMetaData("index", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_ShardIndex.class)));
    tmpMap.put(_Fields.PATTERN, new org.apache.thrift.meta_data.FieldMetaData("pattern", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_TupleNodeId.class)));
    tmpMap.put(_Fields.SUB_PREDS, new org.apache.thrift.meta_data.FieldMetaData("subPreds", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_SubjectPredicateList.class)));
    tmpMap.put(_Fields.ADD_TUPLE, new org.apache.thrift.meta_data.FieldMetaData("addTuple", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_TupleNodeId.class)));
    tmpMap.put(_Fields.DELETE_TUPLE, new org.apache.thrift.meta_data.FieldMetaData("deleteTuple", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_TupleNodeId.class)));
    tmpMap.put(_Fields.PING, new org.apache.thrift.meta_data.FieldMetaData("ping", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_Ping.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLZ_IdxRequest.class, metaDataMap);
  }

  public TLZ_IdxRequest() {
  }

  public TLZ_IdxRequest(
    long requestId,
    TLZ_ShardIndex index)
  {
    this();
    this.requestId = requestId;
    setRequestIdIsSet(true);
    this.index = index;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLZ_IdxRequest(TLZ_IdxRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    this.requestId = other.requestId;
    if (other.isSetIndex()) {
      this.index = new TLZ_ShardIndex(other.index);
    }
    if (other.isSetPattern()) {
      this.pattern = new TLZ_TupleNodeId(other.pattern);
    }
    if (other.isSetSubPreds()) {
      this.subPreds = new TLZ_SubjectPredicateList(other.subPreds);
    }
    if (other.isSetAddTuple()) {
      this.addTuple = new TLZ_TupleNodeId(other.addTuple);
    }
    if (other.isSetDeleteTuple()) {
      this.deleteTuple = new TLZ_TupleNodeId(other.deleteTuple);
    }
    if (other.isSetPing()) {
      this.ping = new TLZ_Ping(other.ping);
    }
  }

  public TLZ_IdxRequest deepCopy() {
    return new TLZ_IdxRequest(this);
  }

  @Override
  public void clear() {
    setRequestIdIsSet(false);
    this.requestId = 0;
    this.index = null;
    this.pattern = null;
    this.subPreds = null;
    this.addTuple = null;
    this.deleteTuple = null;
    this.ping = null;
  }

  public long getRequestId() {
    return this.requestId;
  }

  public TLZ_IdxRequest setRequestId(long requestId) {
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

  public TLZ_ShardIndex getIndex() {
    return this.index;
  }

  public TLZ_IdxRequest setIndex(TLZ_ShardIndex index) {
    this.index = index;
    return this;
  }

  public void unsetIndex() {
    this.index = null;
  }

  /** Returns true if field index is set (has been assigned a value) and false otherwise */
  public boolean isSetIndex() {
    return this.index != null;
  }

  public void setIndexIsSet(boolean value) {
    if (!value) {
      this.index = null;
    }
  }

  public TLZ_TupleNodeId getPattern() {
    return this.pattern;
  }

  public TLZ_IdxRequest setPattern(TLZ_TupleNodeId pattern) {
    this.pattern = pattern;
    return this;
  }

  public void unsetPattern() {
    this.pattern = null;
  }

  /** Returns true if field pattern is set (has been assigned a value) and false otherwise */
  public boolean isSetPattern() {
    return this.pattern != null;
  }

  public void setPatternIsSet(boolean value) {
    if (!value) {
      this.pattern = null;
    }
  }

  public TLZ_SubjectPredicateList getSubPreds() {
    return this.subPreds;
  }

  public TLZ_IdxRequest setSubPreds(TLZ_SubjectPredicateList subPreds) {
    this.subPreds = subPreds;
    return this;
  }

  public void unsetSubPreds() {
    this.subPreds = null;
  }

  /** Returns true if field subPreds is set (has been assigned a value) and false otherwise */
  public boolean isSetSubPreds() {
    return this.subPreds != null;
  }

  public void setSubPredsIsSet(boolean value) {
    if (!value) {
      this.subPreds = null;
    }
  }

  public TLZ_TupleNodeId getAddTuple() {
    return this.addTuple;
  }

  public TLZ_IdxRequest setAddTuple(TLZ_TupleNodeId addTuple) {
    this.addTuple = addTuple;
    return this;
  }

  public void unsetAddTuple() {
    this.addTuple = null;
  }

  /** Returns true if field addTuple is set (has been assigned a value) and false otherwise */
  public boolean isSetAddTuple() {
    return this.addTuple != null;
  }

  public void setAddTupleIsSet(boolean value) {
    if (!value) {
      this.addTuple = null;
    }
  }

  public TLZ_TupleNodeId getDeleteTuple() {
    return this.deleteTuple;
  }

  public TLZ_IdxRequest setDeleteTuple(TLZ_TupleNodeId deleteTuple) {
    this.deleteTuple = deleteTuple;
    return this;
  }

  public void unsetDeleteTuple() {
    this.deleteTuple = null;
  }

  /** Returns true if field deleteTuple is set (has been assigned a value) and false otherwise */
  public boolean isSetDeleteTuple() {
    return this.deleteTuple != null;
  }

  public void setDeleteTupleIsSet(boolean value) {
    if (!value) {
      this.deleteTuple = null;
    }
  }

  public TLZ_Ping getPing() {
    return this.ping;
  }

  public TLZ_IdxRequest setPing(TLZ_Ping ping) {
    this.ping = ping;
    return this;
  }

  public void unsetPing() {
    this.ping = null;
  }

  /** Returns true if field ping is set (has been assigned a value) and false otherwise */
  public boolean isSetPing() {
    return this.ping != null;
  }

  public void setPingIsSet(boolean value) {
    if (!value) {
      this.ping = null;
    }
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

    case INDEX:
      if (value == null) {
        unsetIndex();
      } else {
        setIndex((TLZ_ShardIndex)value);
      }
      break;

    case PATTERN:
      if (value == null) {
        unsetPattern();
      } else {
        setPattern((TLZ_TupleNodeId)value);
      }
      break;

    case SUB_PREDS:
      if (value == null) {
        unsetSubPreds();
      } else {
        setSubPreds((TLZ_SubjectPredicateList)value);
      }
      break;

    case ADD_TUPLE:
      if (value == null) {
        unsetAddTuple();
      } else {
        setAddTuple((TLZ_TupleNodeId)value);
      }
      break;

    case DELETE_TUPLE:
      if (value == null) {
        unsetDeleteTuple();
      } else {
        setDeleteTuple((TLZ_TupleNodeId)value);
      }
      break;

    case PING:
      if (value == null) {
        unsetPing();
      } else {
        setPing((TLZ_Ping)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case REQUEST_ID:
      return Long.valueOf(getRequestId());

    case INDEX:
      return getIndex();

    case PATTERN:
      return getPattern();

    case SUB_PREDS:
      return getSubPreds();

    case ADD_TUPLE:
      return getAddTuple();

    case DELETE_TUPLE:
      return getDeleteTuple();

    case PING:
      return getPing();

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
    case INDEX:
      return isSetIndex();
    case PATTERN:
      return isSetPattern();
    case SUB_PREDS:
      return isSetSubPreds();
    case ADD_TUPLE:
      return isSetAddTuple();
    case DELETE_TUPLE:
      return isSetDeleteTuple();
    case PING:
      return isSetPing();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TLZ_IdxRequest)
      return this.equals((TLZ_IdxRequest)that);
    return false;
  }

  public boolean equals(TLZ_IdxRequest that) {
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

    boolean this_present_index = true && this.isSetIndex();
    boolean that_present_index = true && that.isSetIndex();
    if (this_present_index || that_present_index) {
      if (!(this_present_index && that_present_index))
        return false;
      if (!this.index.equals(that.index))
        return false;
    }

    boolean this_present_pattern = true && this.isSetPattern();
    boolean that_present_pattern = true && that.isSetPattern();
    if (this_present_pattern || that_present_pattern) {
      if (!(this_present_pattern && that_present_pattern))
        return false;
      if (!this.pattern.equals(that.pattern))
        return false;
    }

    boolean this_present_subPreds = true && this.isSetSubPreds();
    boolean that_present_subPreds = true && that.isSetSubPreds();
    if (this_present_subPreds || that_present_subPreds) {
      if (!(this_present_subPreds && that_present_subPreds))
        return false;
      if (!this.subPreds.equals(that.subPreds))
        return false;
    }

    boolean this_present_addTuple = true && this.isSetAddTuple();
    boolean that_present_addTuple = true && that.isSetAddTuple();
    if (this_present_addTuple || that_present_addTuple) {
      if (!(this_present_addTuple && that_present_addTuple))
        return false;
      if (!this.addTuple.equals(that.addTuple))
        return false;
    }

    boolean this_present_deleteTuple = true && this.isSetDeleteTuple();
    boolean that_present_deleteTuple = true && that.isSetDeleteTuple();
    if (this_present_deleteTuple || that_present_deleteTuple) {
      if (!(this_present_deleteTuple && that_present_deleteTuple))
        return false;
      if (!this.deleteTuple.equals(that.deleteTuple))
        return false;
    }

    boolean this_present_ping = true && this.isSetPing();
    boolean that_present_ping = true && that.isSetPing();
    if (this_present_ping || that_present_ping) {
      if (!(this_present_ping && that_present_ping))
        return false;
      if (!this.ping.equals(that.ping))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TLZ_IdxRequest other) {
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
    lastComparison = Boolean.valueOf(isSetIndex()).compareTo(other.isSetIndex());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIndex()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.index, other.index);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPattern()).compareTo(other.isSetPattern());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPattern()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.pattern, other.pattern);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetSubPreds()).compareTo(other.isSetSubPreds());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetSubPreds()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.subPreds, other.subPreds);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAddTuple()).compareTo(other.isSetAddTuple());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAddTuple()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.addTuple, other.addTuple);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetDeleteTuple()).compareTo(other.isSetDeleteTuple());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetDeleteTuple()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.deleteTuple, other.deleteTuple);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPing()).compareTo(other.isSetPing());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPing()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ping, other.ping);
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
    StringBuilder sb = new StringBuilder("TLZ_IdxRequest(");
    boolean first = true;

    sb.append("requestId:");
    sb.append(this.requestId);
    first = false;
    if (!first) sb.append(", ");
    sb.append("index:");
    if (this.index == null) {
      sb.append("null");
    } else {
      sb.append(this.index);
    }
    first = false;
    if (isSetPattern()) {
      if (!first) sb.append(", ");
      sb.append("pattern:");
      if (this.pattern == null) {
        sb.append("null");
      } else {
        sb.append(this.pattern);
      }
      first = false;
    }
    if (isSetSubPreds()) {
      if (!first) sb.append(", ");
      sb.append("subPreds:");
      if (this.subPreds == null) {
        sb.append("null");
      } else {
        sb.append(this.subPreds);
      }
      first = false;
    }
    if (isSetAddTuple()) {
      if (!first) sb.append(", ");
      sb.append("addTuple:");
      if (this.addTuple == null) {
        sb.append("null");
      } else {
        sb.append(this.addTuple);
      }
      first = false;
    }
    if (isSetDeleteTuple()) {
      if (!first) sb.append(", ");
      sb.append("deleteTuple:");
      if (this.deleteTuple == null) {
        sb.append("null");
      } else {
        sb.append(this.deleteTuple);
      }
      first = false;
    }
    if (isSetPing()) {
      if (!first) sb.append(", ");
      sb.append("ping:");
      if (this.ping == null) {
        sb.append("null");
      } else {
        sb.append(this.ping);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'requestId' because it's a primitive and you chose the non-beans generator.
    if (index == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'index' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (index != null) {
      index.validate();
    }
    if (pattern != null) {
      pattern.validate();
    }
    if (subPreds != null) {
      subPreds.validate();
    }
    if (addTuple != null) {
      addTuple.validate();
    }
    if (deleteTuple != null) {
      deleteTuple.validate();
    }
    if (ping != null) {
      ping.validate();
    }
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

  private static class TLZ_IdxRequestStandardSchemeFactory implements SchemeFactory {
    public TLZ_IdxRequestStandardScheme getScheme() {
      return new TLZ_IdxRequestStandardScheme();
    }
  }

  private static class TLZ_IdxRequestStandardScheme extends StandardScheme<TLZ_IdxRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLZ_IdxRequest struct) throws org.apache.thrift.TException {
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
          case 2: // INDEX
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.index = new TLZ_ShardIndex();
              struct.index.read(iprot);
              struct.setIndexIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // PATTERN
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.pattern = new TLZ_TupleNodeId();
              struct.pattern.read(iprot);
              struct.setPatternIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // SUB_PREDS
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.subPreds = new TLZ_SubjectPredicateList();
              struct.subPreds.read(iprot);
              struct.setSubPredsIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // ADD_TUPLE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.addTuple = new TLZ_TupleNodeId();
              struct.addTuple.read(iprot);
              struct.setAddTupleIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // DELETE_TUPLE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.deleteTuple = new TLZ_TupleNodeId();
              struct.deleteTuple.read(iprot);
              struct.setDeleteTupleIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 7: // PING
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.ping = new TLZ_Ping();
              struct.ping.read(iprot);
              struct.setPingIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLZ_IdxRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(REQUEST_ID_FIELD_DESC);
      oprot.writeI64(struct.requestId);
      oprot.writeFieldEnd();
      if (struct.index != null) {
        oprot.writeFieldBegin(INDEX_FIELD_DESC);
        struct.index.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.pattern != null) {
        if (struct.isSetPattern()) {
          oprot.writeFieldBegin(PATTERN_FIELD_DESC);
          struct.pattern.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.subPreds != null) {
        if (struct.isSetSubPreds()) {
          oprot.writeFieldBegin(SUB_PREDS_FIELD_DESC);
          struct.subPreds.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.addTuple != null) {
        if (struct.isSetAddTuple()) {
          oprot.writeFieldBegin(ADD_TUPLE_FIELD_DESC);
          struct.addTuple.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.deleteTuple != null) {
        if (struct.isSetDeleteTuple()) {
          oprot.writeFieldBegin(DELETE_TUPLE_FIELD_DESC);
          struct.deleteTuple.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.ping != null) {
        if (struct.isSetPing()) {
          oprot.writeFieldBegin(PING_FIELD_DESC);
          struct.ping.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TLZ_IdxRequestTupleSchemeFactory implements SchemeFactory {
    public TLZ_IdxRequestTupleScheme getScheme() {
      return new TLZ_IdxRequestTupleScheme();
    }
  }

  private static class TLZ_IdxRequestTupleScheme extends TupleScheme<TLZ_IdxRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLZ_IdxRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI64(struct.requestId);
      struct.index.write(oprot);
      BitSet optionals = new BitSet();
      if (struct.isSetPattern()) {
        optionals.set(0);
      }
      if (struct.isSetSubPreds()) {
        optionals.set(1);
      }
      if (struct.isSetAddTuple()) {
        optionals.set(2);
      }
      if (struct.isSetDeleteTuple()) {
        optionals.set(3);
      }
      if (struct.isSetPing()) {
        optionals.set(4);
      }
      oprot.writeBitSet(optionals, 5);
      if (struct.isSetPattern()) {
        struct.pattern.write(oprot);
      }
      if (struct.isSetSubPreds()) {
        struct.subPreds.write(oprot);
      }
      if (struct.isSetAddTuple()) {
        struct.addTuple.write(oprot);
      }
      if (struct.isSetDeleteTuple()) {
        struct.deleteTuple.write(oprot);
      }
      if (struct.isSetPing()) {
        struct.ping.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLZ_IdxRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.requestId = iprot.readI64();
      struct.setRequestIdIsSet(true);
      struct.index = new TLZ_ShardIndex();
      struct.index.read(iprot);
      struct.setIndexIsSet(true);
      BitSet incoming = iprot.readBitSet(5);
      if (incoming.get(0)) {
        struct.pattern = new TLZ_TupleNodeId();
        struct.pattern.read(iprot);
        struct.setPatternIsSet(true);
      }
      if (incoming.get(1)) {
        struct.subPreds = new TLZ_SubjectPredicateList();
        struct.subPreds.read(iprot);
        struct.setSubPredsIsSet(true);
      }
      if (incoming.get(2)) {
        struct.addTuple = new TLZ_TupleNodeId();
        struct.addTuple.read(iprot);
        struct.setAddTupleIsSet(true);
      }
      if (incoming.get(3)) {
        struct.deleteTuple = new TLZ_TupleNodeId();
        struct.deleteTuple.read(iprot);
        struct.setDeleteTupleIsSet(true);
      }
      if (incoming.get(4)) {
        struct.ping = new TLZ_Ping();
        struct.ping.read(iprot);
        struct.setPingIsSet(true);
      }
    }
  }

}

