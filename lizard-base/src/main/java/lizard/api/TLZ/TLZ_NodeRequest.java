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
public class TLZ_NodeRequest implements org.apache.thrift.TBase<TLZ_NodeRequest, TLZ_NodeRequest._Fields>, java.io.Serializable, Cloneable, Comparable<TLZ_NodeRequest> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLZ_NodeRequest");

  private static final org.apache.thrift.protocol.TField REQUEST_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("requestId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField FIND_BY_NODE_FIELD_DESC = new org.apache.thrift.protocol.TField("findByNode", org.apache.thrift.protocol.TType.STRUCT, (short)3);
  private static final org.apache.thrift.protocol.TField ALLOC_NODE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("allocNodeId", org.apache.thrift.protocol.TType.STRUCT, (short)4);
  private static final org.apache.thrift.protocol.TField FIND_BY_NODE_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("findByNodeId", org.apache.thrift.protocol.TType.STRUCT, (short)5);
  private static final org.apache.thrift.protocol.TField PING_FIELD_DESC = new org.apache.thrift.protocol.TField("ping", org.apache.thrift.protocol.TType.STRUCT, (short)6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TLZ_NodeRequestStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TLZ_NodeRequestTupleSchemeFactory());
  }

  public long requestId; // required
  public TLZ_Node findByNode; // optional
  public TLZ_Node allocNodeId; // optional
  public TLZ_NodeId findByNodeId; // optional
  public TLZ_Ping ping; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    REQUEST_ID((short)1, "requestId"),
    FIND_BY_NODE((short)3, "findByNode"),
    ALLOC_NODE_ID((short)4, "allocNodeId"),
    FIND_BY_NODE_ID((short)5, "findByNodeId"),
    PING((short)6, "ping");

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
        case 3: // FIND_BY_NODE
          return FIND_BY_NODE;
        case 4: // ALLOC_NODE_ID
          return ALLOC_NODE_ID;
        case 5: // FIND_BY_NODE_ID
          return FIND_BY_NODE_ID;
        case 6: // PING
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
  private _Fields optionals[] = {_Fields.FIND_BY_NODE,_Fields.ALLOC_NODE_ID,_Fields.FIND_BY_NODE_ID,_Fields.PING};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.REQUEST_ID, new org.apache.thrift.meta_data.FieldMetaData("requestId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.FIND_BY_NODE, new org.apache.thrift.meta_data.FieldMetaData("findByNode", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_Node.class)));
    tmpMap.put(_Fields.ALLOC_NODE_ID, new org.apache.thrift.meta_data.FieldMetaData("allocNodeId", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_Node.class)));
    tmpMap.put(_Fields.FIND_BY_NODE_ID, new org.apache.thrift.meta_data.FieldMetaData("findByNodeId", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_NodeId.class)));
    tmpMap.put(_Fields.PING, new org.apache.thrift.meta_data.FieldMetaData("ping", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_Ping.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLZ_NodeRequest.class, metaDataMap);
  }

  public TLZ_NodeRequest() {
  }

  public TLZ_NodeRequest(
    long requestId)
  {
    this();
    this.requestId = requestId;
    setRequestIdIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLZ_NodeRequest(TLZ_NodeRequest other) {
    __isset_bitfield = other.__isset_bitfield;
    this.requestId = other.requestId;
    if (other.isSetFindByNode()) {
      this.findByNode = new TLZ_Node(other.findByNode);
    }
    if (other.isSetAllocNodeId()) {
      this.allocNodeId = new TLZ_Node(other.allocNodeId);
    }
    if (other.isSetFindByNodeId()) {
      this.findByNodeId = new TLZ_NodeId(other.findByNodeId);
    }
    if (other.isSetPing()) {
      this.ping = new TLZ_Ping(other.ping);
    }
  }

  public TLZ_NodeRequest deepCopy() {
    return new TLZ_NodeRequest(this);
  }

  @Override
  public void clear() {
    setRequestIdIsSet(false);
    this.requestId = 0;
    this.findByNode = null;
    this.allocNodeId = null;
    this.findByNodeId = null;
    this.ping = null;
  }

  public long getRequestId() {
    return this.requestId;
  }

  public TLZ_NodeRequest setRequestId(long requestId) {
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

  public TLZ_Node getFindByNode() {
    return this.findByNode;
  }

  public TLZ_NodeRequest setFindByNode(TLZ_Node findByNode) {
    this.findByNode = findByNode;
    return this;
  }

  public void unsetFindByNode() {
    this.findByNode = null;
  }

  /** Returns true if field findByNode is set (has been assigned a value) and false otherwise */
  public boolean isSetFindByNode() {
    return this.findByNode != null;
  }

  public void setFindByNodeIsSet(boolean value) {
    if (!value) {
      this.findByNode = null;
    }
  }

  public TLZ_Node getAllocNodeId() {
    return this.allocNodeId;
  }

  public TLZ_NodeRequest setAllocNodeId(TLZ_Node allocNodeId) {
    this.allocNodeId = allocNodeId;
    return this;
  }

  public void unsetAllocNodeId() {
    this.allocNodeId = null;
  }

  /** Returns true if field allocNodeId is set (has been assigned a value) and false otherwise */
  public boolean isSetAllocNodeId() {
    return this.allocNodeId != null;
  }

  public void setAllocNodeIdIsSet(boolean value) {
    if (!value) {
      this.allocNodeId = null;
    }
  }

  public TLZ_NodeId getFindByNodeId() {
    return this.findByNodeId;
  }

  public TLZ_NodeRequest setFindByNodeId(TLZ_NodeId findByNodeId) {
    this.findByNodeId = findByNodeId;
    return this;
  }

  public void unsetFindByNodeId() {
    this.findByNodeId = null;
  }

  /** Returns true if field findByNodeId is set (has been assigned a value) and false otherwise */
  public boolean isSetFindByNodeId() {
    return this.findByNodeId != null;
  }

  public void setFindByNodeIdIsSet(boolean value) {
    if (!value) {
      this.findByNodeId = null;
    }
  }

  public TLZ_Ping getPing() {
    return this.ping;
  }

  public TLZ_NodeRequest setPing(TLZ_Ping ping) {
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

    case FIND_BY_NODE:
      if (value == null) {
        unsetFindByNode();
      } else {
        setFindByNode((TLZ_Node)value);
      }
      break;

    case ALLOC_NODE_ID:
      if (value == null) {
        unsetAllocNodeId();
      } else {
        setAllocNodeId((TLZ_Node)value);
      }
      break;

    case FIND_BY_NODE_ID:
      if (value == null) {
        unsetFindByNodeId();
      } else {
        setFindByNodeId((TLZ_NodeId)value);
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

    case FIND_BY_NODE:
      return getFindByNode();

    case ALLOC_NODE_ID:
      return getAllocNodeId();

    case FIND_BY_NODE_ID:
      return getFindByNodeId();

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
    case FIND_BY_NODE:
      return isSetFindByNode();
    case ALLOC_NODE_ID:
      return isSetAllocNodeId();
    case FIND_BY_NODE_ID:
      return isSetFindByNodeId();
    case PING:
      return isSetPing();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TLZ_NodeRequest)
      return this.equals((TLZ_NodeRequest)that);
    return false;
  }

  public boolean equals(TLZ_NodeRequest that) {
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

    boolean this_present_findByNode = true && this.isSetFindByNode();
    boolean that_present_findByNode = true && that.isSetFindByNode();
    if (this_present_findByNode || that_present_findByNode) {
      if (!(this_present_findByNode && that_present_findByNode))
        return false;
      if (!this.findByNode.equals(that.findByNode))
        return false;
    }

    boolean this_present_allocNodeId = true && this.isSetAllocNodeId();
    boolean that_present_allocNodeId = true && that.isSetAllocNodeId();
    if (this_present_allocNodeId || that_present_allocNodeId) {
      if (!(this_present_allocNodeId && that_present_allocNodeId))
        return false;
      if (!this.allocNodeId.equals(that.allocNodeId))
        return false;
    }

    boolean this_present_findByNodeId = true && this.isSetFindByNodeId();
    boolean that_present_findByNodeId = true && that.isSetFindByNodeId();
    if (this_present_findByNodeId || that_present_findByNodeId) {
      if (!(this_present_findByNodeId && that_present_findByNodeId))
        return false;
      if (!this.findByNodeId.equals(that.findByNodeId))
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
  public int compareTo(TLZ_NodeRequest other) {
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
    lastComparison = Boolean.valueOf(isSetFindByNode()).compareTo(other.isSetFindByNode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFindByNode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.findByNode, other.findByNode);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetAllocNodeId()).compareTo(other.isSetAllocNodeId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAllocNodeId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.allocNodeId, other.allocNodeId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetFindByNodeId()).compareTo(other.isSetFindByNodeId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFindByNodeId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.findByNodeId, other.findByNodeId);
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
    StringBuilder sb = new StringBuilder("TLZ_NodeRequest(");
    boolean first = true;

    sb.append("requestId:");
    sb.append(this.requestId);
    first = false;
    if (isSetFindByNode()) {
      if (!first) sb.append(", ");
      sb.append("findByNode:");
      if (this.findByNode == null) {
        sb.append("null");
      } else {
        sb.append(this.findByNode);
      }
      first = false;
    }
    if (isSetAllocNodeId()) {
      if (!first) sb.append(", ");
      sb.append("allocNodeId:");
      if (this.allocNodeId == null) {
        sb.append("null");
      } else {
        sb.append(this.allocNodeId);
      }
      first = false;
    }
    if (isSetFindByNodeId()) {
      if (!first) sb.append(", ");
      sb.append("findByNodeId:");
      if (this.findByNodeId == null) {
        sb.append("null");
      } else {
        sb.append(this.findByNodeId);
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
    // check for sub-struct validity
    if (findByNode != null) {
      findByNode.validate();
    }
    if (allocNodeId != null) {
      allocNodeId.validate();
    }
    if (findByNodeId != null) {
      findByNodeId.validate();
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

  private static class TLZ_NodeRequestStandardSchemeFactory implements SchemeFactory {
    public TLZ_NodeRequestStandardScheme getScheme() {
      return new TLZ_NodeRequestStandardScheme();
    }
  }

  private static class TLZ_NodeRequestStandardScheme extends StandardScheme<TLZ_NodeRequest> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLZ_NodeRequest struct) throws org.apache.thrift.TException {
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
          case 3: // FIND_BY_NODE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.findByNode = new TLZ_Node();
              struct.findByNode.read(iprot);
              struct.setFindByNodeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // ALLOC_NODE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.allocNodeId = new TLZ_Node();
              struct.allocNodeId.read(iprot);
              struct.setAllocNodeIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // FIND_BY_NODE_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.findByNodeId = new TLZ_NodeId();
              struct.findByNodeId.read(iprot);
              struct.setFindByNodeIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // PING
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLZ_NodeRequest struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(REQUEST_ID_FIELD_DESC);
      oprot.writeI64(struct.requestId);
      oprot.writeFieldEnd();
      if (struct.findByNode != null) {
        if (struct.isSetFindByNode()) {
          oprot.writeFieldBegin(FIND_BY_NODE_FIELD_DESC);
          struct.findByNode.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.allocNodeId != null) {
        if (struct.isSetAllocNodeId()) {
          oprot.writeFieldBegin(ALLOC_NODE_ID_FIELD_DESC);
          struct.allocNodeId.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.findByNodeId != null) {
        if (struct.isSetFindByNodeId()) {
          oprot.writeFieldBegin(FIND_BY_NODE_ID_FIELD_DESC);
          struct.findByNodeId.write(oprot);
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

  private static class TLZ_NodeRequestTupleSchemeFactory implements SchemeFactory {
    public TLZ_NodeRequestTupleScheme getScheme() {
      return new TLZ_NodeRequestTupleScheme();
    }
  }

  private static class TLZ_NodeRequestTupleScheme extends TupleScheme<TLZ_NodeRequest> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLZ_NodeRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI64(struct.requestId);
      BitSet optionals = new BitSet();
      if (struct.isSetFindByNode()) {
        optionals.set(0);
      }
      if (struct.isSetAllocNodeId()) {
        optionals.set(1);
      }
      if (struct.isSetFindByNodeId()) {
        optionals.set(2);
      }
      if (struct.isSetPing()) {
        optionals.set(3);
      }
      oprot.writeBitSet(optionals, 4);
      if (struct.isSetFindByNode()) {
        struct.findByNode.write(oprot);
      }
      if (struct.isSetAllocNodeId()) {
        struct.allocNodeId.write(oprot);
      }
      if (struct.isSetFindByNodeId()) {
        struct.findByNodeId.write(oprot);
      }
      if (struct.isSetPing()) {
        struct.ping.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLZ_NodeRequest struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.requestId = iprot.readI64();
      struct.setRequestIdIsSet(true);
      BitSet incoming = iprot.readBitSet(4);
      if (incoming.get(0)) {
        struct.findByNode = new TLZ_Node();
        struct.findByNode.read(iprot);
        struct.setFindByNodeIsSet(true);
      }
      if (incoming.get(1)) {
        struct.allocNodeId = new TLZ_Node();
        struct.allocNodeId.read(iprot);
        struct.setAllocNodeIdIsSet(true);
      }
      if (incoming.get(2)) {
        struct.findByNodeId = new TLZ_NodeId();
        struct.findByNodeId.read(iprot);
        struct.setFindByNodeIdIsSet(true);
      }
      if (incoming.get(3)) {
        struct.ping = new TLZ_Ping();
        struct.ping.read(iprot);
        struct.setPingIsSet(true);
      }
    }
  }

}

