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
public class TLZ_ShardIndex implements org.apache.thrift.TBase<TLZ_ShardIndex, TLZ_ShardIndex._Fields>, java.io.Serializable, Cloneable, Comparable<TLZ_ShardIndex> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLZ_ShardIndex");

  private static final org.apache.thrift.protocol.TField INDEX_NAME_FIELD_DESC = new org.apache.thrift.protocol.TField("indexName", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField SHARD_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("shardId", org.apache.thrift.protocol.TType.I32, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TLZ_ShardIndexStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TLZ_ShardIndexTupleSchemeFactory());
  }

  /**
   * 
   * @see TLZ_IndexName
   */
  public TLZ_IndexName indexName; // required
  public int shardId; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    /**
     * 
     * @see TLZ_IndexName
     */
    INDEX_NAME((short)1, "indexName"),
    SHARD_ID((short)2, "shardId");

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
        case 1: // INDEX_NAME
          return INDEX_NAME;
        case 2: // SHARD_ID
          return SHARD_ID;
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
  private static final int __SHARDID_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.INDEX_NAME, new org.apache.thrift.meta_data.FieldMetaData("indexName", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.EnumMetaData(org.apache.thrift.protocol.TType.ENUM, TLZ_IndexName.class)));
    tmpMap.put(_Fields.SHARD_ID, new org.apache.thrift.meta_data.FieldMetaData("shardId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLZ_ShardIndex.class, metaDataMap);
  }

  public TLZ_ShardIndex() {
  }

  public TLZ_ShardIndex(
    TLZ_IndexName indexName,
    int shardId)
  {
    this();
    this.indexName = indexName;
    this.shardId = shardId;
    setShardIdIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLZ_ShardIndex(TLZ_ShardIndex other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetIndexName()) {
      this.indexName = other.indexName;
    }
    this.shardId = other.shardId;
  }

  public TLZ_ShardIndex deepCopy() {
    return new TLZ_ShardIndex(this);
  }

  @Override
  public void clear() {
    this.indexName = null;
    setShardIdIsSet(false);
    this.shardId = 0;
  }

  /**
   * 
   * @see TLZ_IndexName
   */
  public TLZ_IndexName getIndexName() {
    return this.indexName;
  }

  /**
   * 
   * @see TLZ_IndexName
   */
  public TLZ_ShardIndex setIndexName(TLZ_IndexName indexName) {
    this.indexName = indexName;
    return this;
  }

  public void unsetIndexName() {
    this.indexName = null;
  }

  /** Returns true if field indexName is set (has been assigned a value) and false otherwise */
  public boolean isSetIndexName() {
    return this.indexName != null;
  }

  public void setIndexNameIsSet(boolean value) {
    if (!value) {
      this.indexName = null;
    }
  }

  public int getShardId() {
    return this.shardId;
  }

  public TLZ_ShardIndex setShardId(int shardId) {
    this.shardId = shardId;
    setShardIdIsSet(true);
    return this;
  }

  public void unsetShardId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __SHARDID_ISSET_ID);
  }

  /** Returns true if field shardId is set (has been assigned a value) and false otherwise */
  public boolean isSetShardId() {
    return EncodingUtils.testBit(__isset_bitfield, __SHARDID_ISSET_ID);
  }

  public void setShardIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __SHARDID_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case INDEX_NAME:
      if (value == null) {
        unsetIndexName();
      } else {
        setIndexName((TLZ_IndexName)value);
      }
      break;

    case SHARD_ID:
      if (value == null) {
        unsetShardId();
      } else {
        setShardId((Integer)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case INDEX_NAME:
      return getIndexName();

    case SHARD_ID:
      return Integer.valueOf(getShardId());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case INDEX_NAME:
      return isSetIndexName();
    case SHARD_ID:
      return isSetShardId();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TLZ_ShardIndex)
      return this.equals((TLZ_ShardIndex)that);
    return false;
  }

  public boolean equals(TLZ_ShardIndex that) {
    if (that == null)
      return false;

    boolean this_present_indexName = true && this.isSetIndexName();
    boolean that_present_indexName = true && that.isSetIndexName();
    if (this_present_indexName || that_present_indexName) {
      if (!(this_present_indexName && that_present_indexName))
        return false;
      if (!this.indexName.equals(that.indexName))
        return false;
    }

    boolean this_present_shardId = true;
    boolean that_present_shardId = true;
    if (this_present_shardId || that_present_shardId) {
      if (!(this_present_shardId && that_present_shardId))
        return false;
      if (this.shardId != that.shardId)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TLZ_ShardIndex other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetIndexName()).compareTo(other.isSetIndexName());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIndexName()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.indexName, other.indexName);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetShardId()).compareTo(other.isSetShardId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetShardId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.shardId, other.shardId);
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
    StringBuilder sb = new StringBuilder("TLZ_ShardIndex(");
    boolean first = true;

    sb.append("indexName:");
    if (this.indexName == null) {
      sb.append("null");
    } else {
      sb.append(this.indexName);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("shardId:");
    sb.append(this.shardId);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (indexName == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'indexName' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'shardId' because it's a primitive and you chose the non-beans generator.
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

  private static class TLZ_ShardIndexStandardSchemeFactory implements SchemeFactory {
    public TLZ_ShardIndexStandardScheme getScheme() {
      return new TLZ_ShardIndexStandardScheme();
    }
  }

  private static class TLZ_ShardIndexStandardScheme extends StandardScheme<TLZ_ShardIndex> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLZ_ShardIndex struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // INDEX_NAME
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.indexName = TLZ_IndexName.findByValue(iprot.readI32());
              struct.setIndexNameIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // SHARD_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.shardId = iprot.readI32();
              struct.setShardIdIsSet(true);
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
      if (!struct.isSetShardId()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'shardId' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLZ_ShardIndex struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.indexName != null) {
        oprot.writeFieldBegin(INDEX_NAME_FIELD_DESC);
        oprot.writeI32(struct.indexName.getValue());
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(SHARD_ID_FIELD_DESC);
      oprot.writeI32(struct.shardId);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TLZ_ShardIndexTupleSchemeFactory implements SchemeFactory {
    public TLZ_ShardIndexTupleScheme getScheme() {
      return new TLZ_ShardIndexTupleScheme();
    }
  }

  private static class TLZ_ShardIndexTupleScheme extends TupleScheme<TLZ_ShardIndex> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLZ_ShardIndex struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI32(struct.indexName.getValue());
      oprot.writeI32(struct.shardId);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLZ_ShardIndex struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.indexName = TLZ_IndexName.findByValue(iprot.readI32());
      struct.setIndexNameIsSet(true);
      struct.shardId = iprot.readI32();
      struct.setShardIdIsSet(true);
    }
  }

}

