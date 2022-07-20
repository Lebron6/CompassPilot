// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: RTKConnectionState.proto

package com.apron.mobilesdk.state;

public final class ProtoRTKConnectionState {
  private ProtoRTKConnectionState() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }
  public interface RTKConnectionStateOrBuilder extends
      // @@protoc_insertion_point(interface_extends:RTKConnectionState)
      com.google.protobuf.MessageLiteOrBuilder {

    /**
     * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
     * @return The enum numeric value on the wire for rtkConnectionStateWithBaseStationReferenceSource.
     */
    int getRtkConnectionStateWithBaseStationReferenceSourceValue();
    /**
     * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
     * @return The rtkConnectionStateWithBaseStationReferenceSource.
     */
    com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource getRtkConnectionStateWithBaseStationReferenceSource();

    /**
     * <pre>
     *基站ID
     * </pre>
     *
     * <code>string baseStationID = 2;</code>
     * @return The baseStationID.
     */
    java.lang.String getBaseStationID();
    /**
     * <pre>
     *基站ID
     * </pre>
     *
     * <code>string baseStationID = 2;</code>
     * @return The bytes for baseStationID.
     */
    com.google.protobuf.ByteString
        getBaseStationIDBytes();

    /**
     * <pre>
     *基站名称
     * </pre>
     *
     * <code>string baseStationName = 3;</code>
     * @return The baseStationName.
     */
    java.lang.String getBaseStationName();
    /**
     * <pre>
     *基站名称
     * </pre>
     *
     * <code>string baseStationName = 3;</code>
     * @return The bytes for baseStationName.
     */
    com.google.protobuf.ByteString
        getBaseStationNameBytes();

    /**
     * <pre>
     *基站信号等级
     * </pre>
     *
     * <code>int32 signalLevel = 4;</code>
     * @return The signalLevel.
     */
    int getSignalLevel();
  }
  /**
   * Protobuf type {@code RTKConnectionState}
   */
  public  static final class RTKConnectionState extends
      com.google.protobuf.GeneratedMessageLite<
          RTKConnectionState, RTKConnectionState.Builder> implements
      // @@protoc_insertion_point(message_implements:RTKConnectionState)
      RTKConnectionStateOrBuilder {
    private RTKConnectionState() {
      baseStationID_ = "";
      baseStationName_ = "";
    }
    /**
     * <pre>
     *飞机与D-RTK的连接状态(仅支持M300 RTK)
     * </pre>
     *
     * Protobuf enum {@code RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource}
     */
    public enum RTKConnectionStateWithBaseStationReferenceSource
        implements com.google.protobuf.Internal.EnumLite {
      /**
       * <pre>
       *飞机 RTK 处于空闲状态。
       * </pre>
       *
       * <code>IDLE = 0;</code>
       */
      IDLE(0),
      /**
       * <pre>
       *飞机正在扫描所有可能连接的基站。
       * </pre>
       *
       * <code>SCANNING = 1;</code>
       */
      SCANNING(1),
      /**
       * <pre>
       *飞行器正在与基站连接。
       * </pre>
       *
       * <code>CONNECTING = 2;</code>
       */
      CONNECTING(2),
      /**
       * <pre>
       *	飞行器与基站连接。
       * </pre>
       *
       * <code>CONNECTED = 3;</code>
       */
      CONNECTED(3),
      /**
       * <pre>
       *	飞机与基站断开连接。
       * </pre>
       *
       * <code>DISCONNECTED = 4;</code>
       */
      DISCONNECTED(4),
      /**
       * <pre>
       *未知基站位置连接状态。
       * </pre>
       *
       * <code>UNKNOWN = 255;</code>
       */
      UNKNOWN(255),
      UNRECOGNIZED(-1),
      ;

      /**
       * <pre>
       *飞机 RTK 处于空闲状态。
       * </pre>
       *
       * <code>IDLE = 0;</code>
       */
      public static final int IDLE_VALUE = 0;
      /**
       * <pre>
       *飞机正在扫描所有可能连接的基站。
       * </pre>
       *
       * <code>SCANNING = 1;</code>
       */
      public static final int SCANNING_VALUE = 1;
      /**
       * <pre>
       *飞行器正在与基站连接。
       * </pre>
       *
       * <code>CONNECTING = 2;</code>
       */
      public static final int CONNECTING_VALUE = 2;
      /**
       * <pre>
       *	飞行器与基站连接。
       * </pre>
       *
       * <code>CONNECTED = 3;</code>
       */
      public static final int CONNECTED_VALUE = 3;
      /**
       * <pre>
       *	飞机与基站断开连接。
       * </pre>
       *
       * <code>DISCONNECTED = 4;</code>
       */
      public static final int DISCONNECTED_VALUE = 4;
      /**
       * <pre>
       *未知基站位置连接状态。
       * </pre>
       *
       * <code>UNKNOWN = 255;</code>
       */
      public static final int UNKNOWN_VALUE = 255;


      @java.lang.Override
      public final int getNumber() {
        if (this == UNRECOGNIZED) {
          throw new java.lang.IllegalArgumentException(
              "Can't get the number of an unknown enum value.");
        }
        return value;
      }

      /**
       * @param value The number of the enum to look for.
       * @return The enum associated with the given number.
       * @deprecated Use {@link #forNumber(int)} instead.
       */
      @java.lang.Deprecated
      public static RTKConnectionStateWithBaseStationReferenceSource valueOf(int value) {
        return forNumber(value);
      }

      public static RTKConnectionStateWithBaseStationReferenceSource forNumber(int value) {
        switch (value) {
          case 0: return IDLE;
          case 1: return SCANNING;
          case 2: return CONNECTING;
          case 3: return CONNECTED;
          case 4: return DISCONNECTED;
          case 255: return UNKNOWN;
          default: return null;
        }
      }

      public static com.google.protobuf.Internal.EnumLiteMap<RTKConnectionStateWithBaseStationReferenceSource>
          internalGetValueMap() {
        return internalValueMap;
      }
      private static final com.google.protobuf.Internal.EnumLiteMap<
          RTKConnectionStateWithBaseStationReferenceSource> internalValueMap =
            new com.google.protobuf.Internal.EnumLiteMap<RTKConnectionStateWithBaseStationReferenceSource>() {
              @java.lang.Override
              public RTKConnectionStateWithBaseStationReferenceSource findValueByNumber(int number) {
                return RTKConnectionStateWithBaseStationReferenceSource.forNumber(number);
              }
            };

      public static com.google.protobuf.Internal.EnumVerifier 
          internalGetVerifier() {
        return RTKConnectionStateWithBaseStationReferenceSourceVerifier.INSTANCE;
      }

      private static final class RTKConnectionStateWithBaseStationReferenceSourceVerifier implements 
           com.google.protobuf.Internal.EnumVerifier { 
              static final com.google.protobuf.Internal.EnumVerifier           INSTANCE = new RTKConnectionStateWithBaseStationReferenceSourceVerifier();
              @java.lang.Override
              public boolean isInRange(int number) {
                return RTKConnectionStateWithBaseStationReferenceSource.forNumber(number) != null;
              }
            };

      private final int value;

      private RTKConnectionStateWithBaseStationReferenceSource(int value) {
        this.value = value;
      }

      // @@protoc_insertion_point(enum_scope:RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource)
    }

    public static final int RTKCONNECTIONSTATEWITHBASESTATIONREFERENCESOURCE_FIELD_NUMBER = 1;
    private int rtkConnectionStateWithBaseStationReferenceSource_;
    /**
     * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
     * @return The enum numeric value on the wire for rtkConnectionStateWithBaseStationReferenceSource.
     */
    @java.lang.Override
    public int getRtkConnectionStateWithBaseStationReferenceSourceValue() {
      return rtkConnectionStateWithBaseStationReferenceSource_;
    }
    /**
     * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
     * @return The rtkConnectionStateWithBaseStationReferenceSource.
     */
    @java.lang.Override
    public com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource getRtkConnectionStateWithBaseStationReferenceSource() {
      com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource result = com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource.forNumber(rtkConnectionStateWithBaseStationReferenceSource_);
      return result == null ? com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource.UNRECOGNIZED : result;
    }
    /**
     * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
     * @param value The enum numeric value on the wire for rtkConnectionStateWithBaseStationReferenceSource to set.
     */
    private void setRtkConnectionStateWithBaseStationReferenceSourceValue(int value) {
        rtkConnectionStateWithBaseStationReferenceSource_ = value;
    }
    /**
     * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
     * @param value The rtkConnectionStateWithBaseStationReferenceSource to set.
     */
    private void setRtkConnectionStateWithBaseStationReferenceSource(com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource value) {
      rtkConnectionStateWithBaseStationReferenceSource_ = value.getNumber();
      
    }
    /**
     * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
     */
    private void clearRtkConnectionStateWithBaseStationReferenceSource() {
      
      rtkConnectionStateWithBaseStationReferenceSource_ = 0;
    }

    public static final int BASESTATIONID_FIELD_NUMBER = 2;
    private java.lang.String baseStationID_;
    /**
     * <pre>
     *基站ID
     * </pre>
     *
     * <code>string baseStationID = 2;</code>
     * @return The baseStationID.
     */
    @java.lang.Override
    public java.lang.String getBaseStationID() {
      return baseStationID_;
    }
    /**
     * <pre>
     *基站ID
     * </pre>
     *
     * <code>string baseStationID = 2;</code>
     * @return The bytes for baseStationID.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getBaseStationIDBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(baseStationID_);
    }
    /**
     * <pre>
     *基站ID
     * </pre>
     *
     * <code>string baseStationID = 2;</code>
     * @param value The baseStationID to set.
     */
    private void setBaseStationID(
        java.lang.String value) {
      java.lang.Class<?> valueClass = value.getClass();
  
      baseStationID_ = value;
    }
    /**
     * <pre>
     *基站ID
     * </pre>
     *
     * <code>string baseStationID = 2;</code>
     */
    private void clearBaseStationID() {
      
      baseStationID_ = getDefaultInstance().getBaseStationID();
    }
    /**
     * <pre>
     *基站ID
     * </pre>
     *
     * <code>string baseStationID = 2;</code>
     * @param value The bytes for baseStationID to set.
     */
    private void setBaseStationIDBytes(
        com.google.protobuf.ByteString value) {
      checkByteStringIsUtf8(value);
      baseStationID_ = value.toStringUtf8();
      
    }

    public static final int BASESTATIONNAME_FIELD_NUMBER = 3;
    private java.lang.String baseStationName_;
    /**
     * <pre>
     *基站名称
     * </pre>
     *
     * <code>string baseStationName = 3;</code>
     * @return The baseStationName.
     */
    @java.lang.Override
    public java.lang.String getBaseStationName() {
      return baseStationName_;
    }
    /**
     * <pre>
     *基站名称
     * </pre>
     *
     * <code>string baseStationName = 3;</code>
     * @return The bytes for baseStationName.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getBaseStationNameBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(baseStationName_);
    }
    /**
     * <pre>
     *基站名称
     * </pre>
     *
     * <code>string baseStationName = 3;</code>
     * @param value The baseStationName to set.
     */
    private void setBaseStationName(
        java.lang.String value) {
      java.lang.Class<?> valueClass = value.getClass();
  
      baseStationName_ = value;
    }
    /**
     * <pre>
     *基站名称
     * </pre>
     *
     * <code>string baseStationName = 3;</code>
     */
    private void clearBaseStationName() {
      
      baseStationName_ = getDefaultInstance().getBaseStationName();
    }
    /**
     * <pre>
     *基站名称
     * </pre>
     *
     * <code>string baseStationName = 3;</code>
     * @param value The bytes for baseStationName to set.
     */
    private void setBaseStationNameBytes(
        com.google.protobuf.ByteString value) {
      checkByteStringIsUtf8(value);
      baseStationName_ = value.toStringUtf8();
      
    }

    public static final int SIGNALLEVEL_FIELD_NUMBER = 4;
    private int signalLevel_;
    /**
     * <pre>
     *基站信号等级
     * </pre>
     *
     * <code>int32 signalLevel = 4;</code>
     * @return The signalLevel.
     */
    @java.lang.Override
    public int getSignalLevel() {
      return signalLevel_;
    }
    /**
     * <pre>
     *基站信号等级
     * </pre>
     *
     * <code>int32 signalLevel = 4;</code>
     * @param value The signalLevel to set.
     */
    private void setSignalLevel(int value) {
      
      signalLevel_ = value;
    }
    /**
     * <pre>
     *基站信号等级
     * </pre>
     *
     * <code>int32 signalLevel = 4;</code>
     */
    private void clearSignalLevel() {
      
      signalLevel_ = 0;
    }

    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input);
    }
    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static Builder newBuilder() {
      return (Builder) DEFAULT_INSTANCE.createBuilder();
    }
    public static Builder newBuilder(com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState prototype) {
      return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
    }

    /**
     * Protobuf type {@code RTKConnectionState}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageLite.Builder<
          com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState, Builder> implements
        // @@protoc_insertion_point(builder_implements:RTKConnectionState)
        com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionStateOrBuilder {
      // Construct using com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState.newBuilder()
      private Builder() {
        super(DEFAULT_INSTANCE);
      }


      /**
       * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
       * @return The enum numeric value on the wire for rtkConnectionStateWithBaseStationReferenceSource.
       */
      @java.lang.Override
      public int getRtkConnectionStateWithBaseStationReferenceSourceValue() {
        return instance.getRtkConnectionStateWithBaseStationReferenceSourceValue();
      }
      /**
       * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
       * @param value The rtkConnectionStateWithBaseStationReferenceSource to set.
       * @return This builder for chaining.
       */
      public Builder setRtkConnectionStateWithBaseStationReferenceSourceValue(int value) {
        copyOnWrite();
        instance.setRtkConnectionStateWithBaseStationReferenceSourceValue(value);
        return this;
      }
      /**
       * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
       * @return The rtkConnectionStateWithBaseStationReferenceSource.
       */
      @java.lang.Override
      public com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource getRtkConnectionStateWithBaseStationReferenceSource() {
        return instance.getRtkConnectionStateWithBaseStationReferenceSource();
      }
      /**
       * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
       * @param value The enum numeric value on the wire for rtkConnectionStateWithBaseStationReferenceSource to set.
       * @return This builder for chaining.
       */
      public Builder setRtkConnectionStateWithBaseStationReferenceSource(com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource value) {
        copyOnWrite();
        instance.setRtkConnectionStateWithBaseStationReferenceSource(value);
        return this;
      }
      /**
       * <code>.RTKConnectionState.RTKConnectionStateWithBaseStationReferenceSource rtkConnectionStateWithBaseStationReferenceSource = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearRtkConnectionStateWithBaseStationReferenceSource() {
        copyOnWrite();
        instance.clearRtkConnectionStateWithBaseStationReferenceSource();
        return this;
      }

      /**
       * <pre>
       *基站ID
       * </pre>
       *
       * <code>string baseStationID = 2;</code>
       * @return The baseStationID.
       */
      @java.lang.Override
      public java.lang.String getBaseStationID() {
        return instance.getBaseStationID();
      }
      /**
       * <pre>
       *基站ID
       * </pre>
       *
       * <code>string baseStationID = 2;</code>
       * @return The bytes for baseStationID.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString
          getBaseStationIDBytes() {
        return instance.getBaseStationIDBytes();
      }
      /**
       * <pre>
       *基站ID
       * </pre>
       *
       * <code>string baseStationID = 2;</code>
       * @param value The baseStationID to set.
       * @return This builder for chaining.
       */
      public Builder setBaseStationID(
          java.lang.String value) {
        copyOnWrite();
        instance.setBaseStationID(value);
        return this;
      }
      /**
       * <pre>
       *基站ID
       * </pre>
       *
       * <code>string baseStationID = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearBaseStationID() {
        copyOnWrite();
        instance.clearBaseStationID();
        return this;
      }
      /**
       * <pre>
       *基站ID
       * </pre>
       *
       * <code>string baseStationID = 2;</code>
       * @param value The bytes for baseStationID to set.
       * @return This builder for chaining.
       */
      public Builder setBaseStationIDBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setBaseStationIDBytes(value);
        return this;
      }

      /**
       * <pre>
       *基站名称
       * </pre>
       *
       * <code>string baseStationName = 3;</code>
       * @return The baseStationName.
       */
      @java.lang.Override
      public java.lang.String getBaseStationName() {
        return instance.getBaseStationName();
      }
      /**
       * <pre>
       *基站名称
       * </pre>
       *
       * <code>string baseStationName = 3;</code>
       * @return The bytes for baseStationName.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString
          getBaseStationNameBytes() {
        return instance.getBaseStationNameBytes();
      }
      /**
       * <pre>
       *基站名称
       * </pre>
       *
       * <code>string baseStationName = 3;</code>
       * @param value The baseStationName to set.
       * @return This builder for chaining.
       */
      public Builder setBaseStationName(
          java.lang.String value) {
        copyOnWrite();
        instance.setBaseStationName(value);
        return this;
      }
      /**
       * <pre>
       *基站名称
       * </pre>
       *
       * <code>string baseStationName = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearBaseStationName() {
        copyOnWrite();
        instance.clearBaseStationName();
        return this;
      }
      /**
       * <pre>
       *基站名称
       * </pre>
       *
       * <code>string baseStationName = 3;</code>
       * @param value The bytes for baseStationName to set.
       * @return This builder for chaining.
       */
      public Builder setBaseStationNameBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setBaseStationNameBytes(value);
        return this;
      }

      /**
       * <pre>
       *基站信号等级
       * </pre>
       *
       * <code>int32 signalLevel = 4;</code>
       * @return The signalLevel.
       */
      @java.lang.Override
      public int getSignalLevel() {
        return instance.getSignalLevel();
      }
      /**
       * <pre>
       *基站信号等级
       * </pre>
       *
       * <code>int32 signalLevel = 4;</code>
       * @param value The signalLevel to set.
       * @return This builder for chaining.
       */
      public Builder setSignalLevel(int value) {
        copyOnWrite();
        instance.setSignalLevel(value);
        return this;
      }
      /**
       * <pre>
       *基站信号等级
       * </pre>
       *
       * <code>int32 signalLevel = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearSignalLevel() {
        copyOnWrite();
        instance.clearSignalLevel();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:RTKConnectionState)
    }
    @java.lang.Override
    @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
    protected final java.lang.Object dynamicMethod(
        com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
        java.lang.Object arg0, java.lang.Object arg1) {
      switch (method) {
        case NEW_MUTABLE_INSTANCE: {
          return new com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState();
        }
        case NEW_BUILDER: {
          return new Builder();
        }
        case BUILD_MESSAGE_INFO: {
            java.lang.Object[] objects = new java.lang.Object[] {
              "rtkConnectionStateWithBaseStationReferenceSource_",
              "baseStationID_",
              "baseStationName_",
              "signalLevel_",
            };
            java.lang.String info =
                "\u0000\u0004\u0000\u0000\u0001\u0004\u0004\u0000\u0000\u0000\u0001\f\u0002\u0208" +
                "\u0003\u0208\u0004\u0004";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
        }
        // fall through
        case GET_DEFAULT_INSTANCE: {
          return DEFAULT_INSTANCE;
        }
        case GET_PARSER: {
          com.google.protobuf.Parser<com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState> parser = PARSER;
          if (parser == null) {
            synchronized (com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState.class) {
              parser = PARSER;
              if (parser == null) {
                parser =
                    new DefaultInstanceBasedParser<com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState>(
                        DEFAULT_INSTANCE);
                PARSER = parser;
              }
            }
          }
          return parser;
      }
      case GET_MEMOIZED_IS_INITIALIZED: {
        return (byte) 1;
      }
      case SET_MEMOIZED_IS_INITIALIZED: {
        return null;
      }
      }
      throw new UnsupportedOperationException();
    }


    // @@protoc_insertion_point(class_scope:RTKConnectionState)
    private static final com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState DEFAULT_INSTANCE;
    static {
      RTKConnectionState defaultInstance = new RTKConnectionState();
      // New instances are implicitly immutable so no need to make
      // immutable.
      DEFAULT_INSTANCE = defaultInstance;
      com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
        RTKConnectionState.class, defaultInstance);
    }

    public static com.apron.mobilesdk.state.ProtoRTKConnectionState.RTKConnectionState getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static volatile com.google.protobuf.Parser<RTKConnectionState> PARSER;

    public static com.google.protobuf.Parser<RTKConnectionState> parser() {
      return DEFAULT_INSTANCE.getParserForType();
    }
  }


  static {
  }

  // @@protoc_insertion_point(outer_class_scope)
}
