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
public class TLZ_RDF_PrefixDecl implements org.apache.thrift.TBase<TLZ_RDF_PrefixDecl, TLZ_RDF_PrefixDecl._Fields>, java.io.Serializable, Cloneable, Comparable<TLZ_RDF_PrefixDecl> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TLZ_RDF_PrefixDecl");

  private static final org.apache.thrift.protocol.TField PREFIX_FIELD_DESC = new org.apache.thrift.protocol.TField("prefix", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField URI_FIELD_DESC = new org.apache.thrift.protocol.TField("uri", org.apache.thrift.protocol.TType.STRING, (short)2);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TLZ_RDF_PrefixDeclStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TLZ_RDF_PrefixDeclTupleSchemeFactory());
  }

  public String prefix; // required
  public String uri; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    PREFIX((short)1, "prefix"),
    URI((short)2, "uri");

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
        case 1: // PREFIX
          return PREFIX;
        case 2: // URI
          return URI;
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
    tmpMap.put(_Fields.PREFIX, new org.apache.thrift.meta_data.FieldMetaData("prefix", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.URI, new org.apache.thrift.meta_data.FieldMetaData("uri", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TLZ_RDF_PrefixDecl.class, metaDataMap);
  }

  public TLZ_RDF_PrefixDecl() {
  }

  public TLZ_RDF_PrefixDecl(
    String prefix,
    String uri)
  {
    this();
    this.prefix = prefix;
    this.uri = uri;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TLZ_RDF_PrefixDecl(TLZ_RDF_PrefixDecl other) {
    if (other.isSetPrefix()) {
      this.prefix = other.prefix;
    }
    if (other.isSetUri()) {
      this.uri = other.uri;
    }
  }

  public TLZ_RDF_PrefixDecl deepCopy() {
    return new TLZ_RDF_PrefixDecl(this);
  }

  @Override
  public void clear() {
    this.prefix = null;
    this.uri = null;
  }

  public String getPrefix() {
    return this.prefix;
  }

  public TLZ_RDF_PrefixDecl setPrefix(String prefix) {
    this.prefix = prefix;
    return this;
  }

  public void unsetPrefix() {
    this.prefix = null;
  }

  /** Returns true if field prefix is set (has been assigned a value) and false otherwise */
  public boolean isSetPrefix() {
    return this.prefix != null;
  }

  public void setPrefixIsSet(boolean value) {
    if (!value) {
      this.prefix = null;
    }
  }

  public String getUri() {
    return this.uri;
  }

  public TLZ_RDF_PrefixDecl setUri(String uri) {
    this.uri = uri;
    return this;
  }

  public void unsetUri() {
    this.uri = null;
  }

  /** Returns true if field uri is set (has been assigned a value) and false otherwise */
  public boolean isSetUri() {
    return this.uri != null;
  }

  public void setUriIsSet(boolean value) {
    if (!value) {
      this.uri = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case PREFIX:
      if (value == null) {
        unsetPrefix();
      } else {
        setPrefix((String)value);
      }
      break;

    case URI:
      if (value == null) {
        unsetUri();
      } else {
        setUri((String)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case PREFIX:
      return getPrefix();

    case URI:
      return getUri();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case PREFIX:
      return isSetPrefix();
    case URI:
      return isSetUri();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TLZ_RDF_PrefixDecl)
      return this.equals((TLZ_RDF_PrefixDecl)that);
    return false;
  }

  public boolean equals(TLZ_RDF_PrefixDecl that) {
    if (that == null)
      return false;

    boolean this_present_prefix = true && this.isSetPrefix();
    boolean that_present_prefix = true && that.isSetPrefix();
    if (this_present_prefix || that_present_prefix) {
      if (!(this_present_prefix && that_present_prefix))
        return false;
      if (!this.prefix.equals(that.prefix))
        return false;
    }

    boolean this_present_uri = true && this.isSetUri();
    boolean that_present_uri = true && that.isSetUri();
    if (this_present_uri || that_present_uri) {
      if (!(this_present_uri && that_present_uri))
        return false;
      if (!this.uri.equals(that.uri))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @Override
  public int compareTo(TLZ_RDF_PrefixDecl other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetPrefix()).compareTo(other.isSetPrefix());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPrefix()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.prefix, other.prefix);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetUri()).compareTo(other.isSetUri());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetUri()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.uri, other.uri);
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
    StringBuilder sb = new StringBuilder("TLZ_RDF_PrefixDecl(");
    boolean first = true;

    sb.append("prefix:");
    if (this.prefix == null) {
      sb.append("null");
    } else {
      sb.append(this.prefix);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("uri:");
    if (this.uri == null) {
      sb.append("null");
    } else {
      sb.append(this.uri);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (prefix == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'prefix' was not present! Struct: " + toString());
    }
    if (uri == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'uri' was not present! Struct: " + toString());
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

  private static class TLZ_RDF_PrefixDeclStandardSchemeFactory implements SchemeFactory {
    public TLZ_RDF_PrefixDeclStandardScheme getScheme() {
      return new TLZ_RDF_PrefixDeclStandardScheme();
    }
  }

  private static class TLZ_RDF_PrefixDeclStandardScheme extends StandardScheme<TLZ_RDF_PrefixDecl> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TLZ_RDF_PrefixDecl struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // PREFIX
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.prefix = iprot.readString();
              struct.setPrefixIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // URI
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.uri = iprot.readString();
              struct.setUriIsSet(true);
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

    public void write(org.apache.thrift.protocol.TProtocol oprot, TLZ_RDF_PrefixDecl struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.prefix != null) {
        oprot.writeFieldBegin(PREFIX_FIELD_DESC);
        oprot.writeString(struct.prefix);
        oprot.writeFieldEnd();
      }
      if (struct.uri != null) {
        oprot.writeFieldBegin(URI_FIELD_DESC);
        oprot.writeString(struct.uri);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TLZ_RDF_PrefixDeclTupleSchemeFactory implements SchemeFactory {
    public TLZ_RDF_PrefixDeclTupleScheme getScheme() {
      return new TLZ_RDF_PrefixDeclTupleScheme();
    }
  }

  private static class TLZ_RDF_PrefixDeclTupleScheme extends TupleScheme<TLZ_RDF_PrefixDecl> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TLZ_RDF_PrefixDecl struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.prefix);
      oprot.writeString(struct.uri);
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TLZ_RDF_PrefixDecl struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.prefix = iprot.readString();
      struct.setPrefixIsSet(true);
      struct.uri = iprot.readString();
      struct.setUriIsSet(true);
    }
  }

}
