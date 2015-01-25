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
public class TLZ_NodeReply implements org.apache.thrift.TBase<TLZ_NodeReply, TLZ_NodeReply._Fields>, java.io.Serializable, Cloneable, Comparable<TLZ_NodeReply> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLZ_NodeReply");

  private static final org.apache.thrift.protocol.TField REQUEST_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("requestId", org.apache.thrift.protocol.TType.I64, (short)1);
  private static final org.apache.thrift.protocol.TField ALLOC_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("allocId", org.apache.thrift.protocol.TType.STRUCT, (short)2);
  private static final org.apache.thrift.protocol.TField FOUND_NODE_FIELD_DESC = new org.apache.thrift.protocol.TField("foundNode", org.apache.thrift.protocol.TType.STRUCT, (short)3);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TLZ_NodeReplyStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TLZ_NodeReplyTupleSchemeFactory());
  }

  public long requestId; // required
  public TLZ_NodeId allocId; // optional
  public TLZ_Node foundNode; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    REQUEST_ID((short)1, "requestId"),
    ALLOC_ID((short)2, "allocId"),
    FOUND_NODE((short)3, "foundNode");

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
        case 2: // ALLOC_ID
          return ALLOC_ID;
        case 3: // FOUND_NODE
          return FOUND_NODE;
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
  private _Fields optionals[] = {_Fields.ALLOC_ID,_Fields.FOUND_NODE};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.REQUEST_ID, new org.apache.thrift.meta_data.FieldMetaData("requestId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    tmpMap.put(_Fields.ALLOC_ID, new org.apache.thrift.meta_data.FieldMetaData("allocId", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_NodeId.class)));
    tmpMap.put(_Fields.FOUND_NODE, new org.apache.thrift.meta_data.FieldMetaData("foundNode", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TLZ_Node.class)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLZ_NodeReply.class, metaDataMap);
  }

  public TLZ_NodeReply() {
  }

  public TLZ_NodeReply(
    long requestId)
  {
    this();
    this.requestId = requestId;
    setRequestIdIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLZ_NodeReply(TLZ_NodeReply other) {
    __isset_bitfield = other.__isset_bitfield;
    this.requestId = other.requestId;
    if (other.isSetAllocId()) {
      this.allocId = new TLZ_NodeId(other.allocId);
    }
    if (other.isSetFoundNode()) {
      this.foundNode = new TLZ_Node(other.foundNode);
    }
  }

  public TLZ_NodeReply deepCopy() {
    return new TLZ_NodeReply(this);
  }

  @Override
  public void clear() {
    setRequestIdIsSet(false);
    this.requestId = 0;
    this.allocId = null;
    this.foundNode = null;
  }

  public long getRequestId() {
    return this.requestId;
  }

  public TLZ_NodeReply setRequestId(long requestId) {
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

  public TLZ_NodeId getAllocId() {
    return this.allocId;
  }

  public TLZ_NodeReply setAllocId(TLZ_NodeId allocId) {
    this.allocId = allocId;
    return this;
  }

  public void unsetAllocId() {
    this.allocId = null;
  }

  /** Returns true if field allocId is set (has been assigned a value) and false otherwise */
  public boolean isSetAllocId() {
    return this.allocId != null;
  }

  public void setAllocIdIsSet(boolean value) {
    if (!value) {
      this.allocId = null;
    }
  }

  public TLZ_Node getFoundNode() {
    return this.foundNode;
  }

  public TLZ_NodeReply setFoundNode(TLZ_Node foundNode) {
    this.foundNode = foundNode;
    return this;
  }

  public void unsetFoundNode() {
    this.foundNode = null;
  }

  /** Returns true if field foundNode is set (has been assigned a value) and false otherwise */
  public boolean isSetFoundNode() {
    return this.foundNode != null;
  }

  public void setFoundNodeIsSet(boolean value) {
    if (!value) {
      this.foundNode = null;
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

    case ALLOC_ID:
      if (value == null) {
        unsetAllocId();
      } else {
        setAllocId((TLZ_NodeId)value);
      }
      break;

    case FOUND_NODE:
      if (value == null) {
        unsetFoundNode();
      } else {
        setFoundNode((TLZ_Node)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case REQUEST_ID:
      return Long.valueOf(getRequestId());

    case ALLOC_ID:
      return getAllocId();

    case FOUND_NODE:
      return getFoundNode();

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
    case ALLOC_ID:
      return isSetAllocId();
    case FOUND_NODE:
      return isSetFoundNode();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TLZ_NodeReply)
      return this.equals((TLZ_NodeReply)that);
    return false;
  }

  public boolean equals(TLZ_NodeReply that) {
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

    boolean this_present_allocId = true && this.isSetAllocId();
    boolean that_present_allocId = true && that.isSetAllocId();
    if (this_present_allocId || that_present_allocId) {
      if (!(this_present_allocId && that_present_allocId))
        return false;
      if (!this.allocId.equals(that.allocId))
        return false;
    }

    boolean this_present_foundNode = true && this.isSetFoundNode();
    boolean that_present_foundNode = true && that.isSetFoundNode();
    if (this_present_foundNode || that_present_foundNode) {
      if (!(this_present_foundNode && that_present_foundNode))
        return false;
      if (!this.foundNode.equals(that.foundNode))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TLZ_NodeReply other) {
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
    lastComparison = Boolean.valueOf(isSetAllocId()).compareTo(other.isSetAllocId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAllocId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.allocId, other.allocId);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetFoundNode()).compareTo(other.isSetFoundNode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetFoundNode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.foundNode, other.foundNode);
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
    StringBuilder sb = new StringBuilder("TLZ_NodeReply(");
    boolean first = true;

    sb.append("requestId:");
    sb.append(this.requestId);
    first = false;
    if (isSetAllocId()) {
      if (!first) sb.append(", ");
      sb.append("allocId:");
      if (this.allocId == null) {
        sb.append("null");
      } else {
        sb.append(this.allocId);
      }
      first = false;
    }
    if (isSetFoundNode()) {
      if (!first) sb.append(", ");
      sb.append("foundNode:");
      if (this.foundNode == null) {
        sb.append("null");
      } else {
        sb.append(this.foundNode);
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
    if (allocId != null) {
      allocId.validate();
    }
    if (foundNode != null) {
      foundNode.validate();
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

  private static class TLZ_NodeReplyStandardSchemeFactory implements SchemeFactory {
    public TLZ_NodeReplyStandardScheme getScheme() {
      return new TLZ_NodeReplyStandardScheme();
    }
  }

  private static class TLZ_NodeReplyStandardScheme extends StandardScheme<TLZ_NodeReply> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLZ_NodeReply struct) throws org.apache.thrift.TException {
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
          case 2: // ALLOC_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.allocId = new TLZ_NodeId();
              struct.allocId.read(iprot);
              struct.setAllocIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // FOUND_NODE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.foundNode = new TLZ_Node();
              struct.foundNode.read(iprot);
              struct.setFoundNodeIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLZ_NodeReply struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(REQUEST_ID_FIELD_DESC);
      oprot.writeI64(struct.requestId);
      oprot.writeFieldEnd();
      if (struct.allocId != null) {
        if (struct.isSetAllocId()) {
          oprot.writeFieldBegin(ALLOC_ID_FIELD_DESC);
          struct.allocId.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      if (struct.foundNode != null) {
        if (struct.isSetFoundNode()) {
          oprot.writeFieldBegin(FOUND_NODE_FIELD_DESC);
          struct.foundNode.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TLZ_NodeReplyTupleSchemeFactory implements SchemeFactory {
    public TLZ_NodeReplyTupleScheme getScheme() {
      return new TLZ_NodeReplyTupleScheme();
    }
  }

  private static class TLZ_NodeReplyTupleScheme extends TupleScheme<TLZ_NodeReply> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLZ_NodeReply struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI64(struct.requestId);
      BitSet optionals = new BitSet();
      if (struct.isSetAllocId()) {
        optionals.set(0);
      }
      if (struct.isSetFoundNode()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetAllocId()) {
        struct.allocId.write(oprot);
      }
      if (struct.isSetFoundNode()) {
        struct.foundNode.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLZ_NodeReply struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.requestId = iprot.readI64();
      struct.setRequestIdIsSet(true);
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.allocId = new TLZ_NodeId();
        struct.allocId.read(iprot);
        struct.setAllocIdIsSet(true);
      }
      if (incoming.get(1)) {
        struct.foundNode = new TLZ_Node();
        struct.foundNode.read(iprot);
        struct.setFoundNodeIsSet(true);
      }
    }
  }

}

