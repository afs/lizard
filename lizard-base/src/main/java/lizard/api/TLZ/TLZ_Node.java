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
public class TLZ_Node implements org.apache.thrift.TBase<TLZ_Node, TLZ_Node._Fields>, java.io.Serializable, Cloneable, Comparable<TLZ_Node> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLZ_Node");

  private static final org.apache.thrift.protocol.TField NODE_STR_FIELD_DESC = new org.apache.thrift.protocol.TField("nodeStr", org.apache.thrift.protocol.TType.STRING, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TLZ_NodeStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TLZ_NodeTupleSchemeFactory());
  }

  public String nodeStr; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    NODE_STR((short)1, "nodeStr");

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
        case 1: // NODE_STR
          return NODE_STR;
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
    tmpMap.put(_Fields.NODE_STR, new org.apache.thrift.meta_data.FieldMetaData("nodeStr", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLZ_Node.class, metaDataMap);
  }

  public TLZ_Node() {
  }

  public TLZ_Node(
    String nodeStr)
  {
    this();
    this.nodeStr = nodeStr;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLZ_Node(TLZ_Node other) {
    if (other.isSetNodeStr()) {
      this.nodeStr = other.nodeStr;
    }
  }

  public TLZ_Node deepCopy() {
    return new TLZ_Node(this);
  }

  @Override
  public void clear() {
    this.nodeStr = null;
  }

  public String getNodeStr() {
    return this.nodeStr;
  }

  public TLZ_Node setNodeStr(String nodeStr) {
    this.nodeStr = nodeStr;
    return this;
  }

  public void unsetNodeStr() {
    this.nodeStr = null;
  }

  /** Returns true if field nodeStr is set (has been assigned a value) and false otherwise */
  public boolean isSetNodeStr() {
    return this.nodeStr != null;
  }

  public void setNodeStrIsSet(boolean value) {
    if (!value) {
      this.nodeStr = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case NODE_STR:
      if (value == null) {
        unsetNodeStr();
      } else {
        setNodeStr((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case NODE_STR:
      return getNodeStr();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case NODE_STR:
      return isSetNodeStr();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TLZ_Node)
      return this.equals((TLZ_Node)that);
    return false;
  }

  public boolean equals(TLZ_Node that) {
    if (that == null)
      return false;

    boolean this_present_nodeStr = true && this.isSetNodeStr();
    boolean that_present_nodeStr = true && that.isSetNodeStr();
    if (this_present_nodeStr || that_present_nodeStr) {
      if (!(this_present_nodeStr && that_present_nodeStr))
        return false;
      if (!this.nodeStr.equals(that.nodeStr))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TLZ_Node other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetNodeStr()).compareTo(other.isSetNodeStr());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNodeStr()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.nodeStr, other.nodeStr);
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
    StringBuilder sb = new StringBuilder("TLZ_Node(");
    boolean first = true;

    sb.append("nodeStr:");
    if (this.nodeStr == null) {
      sb.append("null");
    } else {
      sb.append(this.nodeStr);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (nodeStr == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'nodeStr' was not present! Struct: " + toString());
    }
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

  private static class TLZ_NodeStandardSchemeFactory implements SchemeFactory {
    public TLZ_NodeStandardScheme getScheme() {
      return new TLZ_NodeStandardScheme();
    }
  }

  private static class TLZ_NodeStandardScheme extends StandardScheme<TLZ_Node> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLZ_Node struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // NODE_STR
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.nodeStr = iprot.readString();
              struct.setNodeStrIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLZ_Node struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.nodeStr != null) {
        oprot.writeFieldBegin(NODE_STR_FIELD_DESC);
        oprot.writeString(struct.nodeStr);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TLZ_NodeTupleSchemeFactory implements SchemeFactory {
    public TLZ_NodeTupleScheme getScheme() {
      return new TLZ_NodeTupleScheme();
    }
  }

  private static class TLZ_NodeTupleScheme extends TupleScheme<TLZ_Node> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLZ_Node struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.nodeStr);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLZ_Node struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.nodeStr = iprot.readString();
      struct.setNodeStrIsSet(true);
    }
  }

}

