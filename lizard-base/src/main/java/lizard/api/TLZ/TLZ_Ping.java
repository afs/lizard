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
public class TLZ_Ping implements org.apache.thrift.TBase<TLZ_Ping, TLZ_Ping._Fields>, java.io.Serializable, Cloneable, Comparable<TLZ_Ping> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLZ_Ping");

  private static final org.apache.thrift.protocol.TField MARKER_FIELD_DESC = new org.apache.thrift.protocol.TField("marker", org.apache.thrift.protocol.TType.I64, (short)1);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TLZ_PingStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TLZ_PingTupleSchemeFactory());
  }

  public long marker; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    MARKER((short)1, "marker");

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
        case 1: // MARKER
          return MARKER;
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
  private static final int __MARKER_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.MARKER, new org.apache.thrift.meta_data.FieldMetaData("marker", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I64)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLZ_Ping.class, metaDataMap);
  }

  public TLZ_Ping() {
  }

  public TLZ_Ping(
    long marker)
  {
    this();
    this.marker = marker;
    setMarkerIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLZ_Ping(TLZ_Ping other) {
    __isset_bitfield = other.__isset_bitfield;
    this.marker = other.marker;
  }

  public TLZ_Ping deepCopy() {
    return new TLZ_Ping(this);
  }

  @Override
  public void clear() {
    setMarkerIsSet(false);
    this.marker = 0;
  }

  public long getMarker() {
    return this.marker;
  }

  public TLZ_Ping setMarker(long marker) {
    this.marker = marker;
    setMarkerIsSet(true);
    return this;
  }

  public void unsetMarker() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __MARKER_ISSET_ID);
  }

  /** Returns true if field marker is set (has been assigned a value) and false otherwise */
  public boolean isSetMarker() {
    return EncodingUtils.testBit(__isset_bitfield, __MARKER_ISSET_ID);
  }

  public void setMarkerIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __MARKER_ISSET_ID, value);
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case MARKER:
      if (value == null) {
        unsetMarker();
      } else {
        setMarker((Long)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case MARKER:
      return Long.valueOf(getMarker());

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case MARKER:
      return isSetMarker();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TLZ_Ping)
      return this.equals((TLZ_Ping)that);
    return false;
  }

  public boolean equals(TLZ_Ping that) {
    if (that == null)
      return false;

    boolean this_present_marker = true;
    boolean that_present_marker = true;
    if (this_present_marker || that_present_marker) {
      if (!(this_present_marker && that_present_marker))
        return false;
      if (this.marker != that.marker)
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TLZ_Ping other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetMarker()).compareTo(other.isSetMarker());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetMarker()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.marker, other.marker);
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
    StringBuilder sb = new StringBuilder("TLZ_Ping(");
    boolean first = true;

    sb.append("marker:");
    sb.append(this.marker);
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'marker' because it's a primitive and you chose the non-beans generator.
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

  private static class TLZ_PingStandardSchemeFactory implements SchemeFactory {
    public TLZ_PingStandardScheme getScheme() {
      return new TLZ_PingStandardScheme();
    }
  }

  private static class TLZ_PingStandardScheme extends StandardScheme<TLZ_Ping> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLZ_Ping struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // MARKER
            if (schemeField.type == org.apache.thrift.protocol.TType.I64) {
              struct.marker = iprot.readI64();
              struct.setMarkerIsSet(true);
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
      if (!struct.isSetMarker()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'marker' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLZ_Ping struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(MARKER_FIELD_DESC);
      oprot.writeI64(struct.marker);
      oprot.writeFieldEnd();
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TLZ_PingTupleSchemeFactory implements SchemeFactory {
    public TLZ_PingTupleScheme getScheme() {
      return new TLZ_PingTupleScheme();
    }
  }

  private static class TLZ_PingTupleScheme extends TupleScheme<TLZ_Ping> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLZ_Ping struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI64(struct.marker);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLZ_Ping struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.marker = iprot.readI64();
      struct.setMarkerIsSet(true);
    }
  }

}

