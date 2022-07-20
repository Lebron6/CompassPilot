// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: Register.proto

package com.apron.mobilesdk.state;

public final class ProtoRegister {
  private ProtoRegister() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }
  public interface RegisterOrBuilder extends
      // @@protoc_insertion_point(interface_extends:Register)
      com.google.protobuf.MessageLiteOrBuilder {

    /**
     * <code>string method = 1;</code>
     * @return The method.
     */
    java.lang.String getMethod();
    /**
     * <code>string method = 1;</code>
     * @return The bytes for method.
     */
    com.google.protobuf.ByteString
        getMethodBytes();

    /**
     * <code>string requestTime = 2;</code>
     * @return The requestTime.
     */
    java.lang.String getRequestTime();
    /**
     * <code>string requestTime = 2;</code>
     * @return The bytes for requestTime.
     */
    com.google.protobuf.ByteString
        getRequestTimeBytes();

    /**
     * <code>string requestId = 3;</code>
     * @return The requestId.
     */
    java.lang.String getRequestId();
    /**
     * <code>string requestId = 3;</code>
     * @return The bytes for requestId.
     */
    com.google.protobuf.ByteString
        getRequestIdBytes();
  }
  /**
   * Protobuf type {@code Register}
   */
  public  static final class Register extends
      com.google.protobuf.GeneratedMessageLite<
          Register, Register.Builder> implements
      // @@protoc_insertion_point(message_implements:Register)
      RegisterOrBuilder {
    private Register() {
      method_ = "";
      requestTime_ = "";
      requestId_ = "";
    }
    public static final int METHOD_FIELD_NUMBER = 1;
    private java.lang.String method_;
    /**
     * <code>string method = 1;</code>
     * @return The method.
     */
    @java.lang.Override
    public java.lang.String getMethod() {
      return method_;
    }
    /**
     * <code>string method = 1;</code>
     * @return The bytes for method.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getMethodBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(method_);
    }
    /**
     * <code>string method = 1;</code>
     * @param value The method to set.
     */
    private void setMethod(
        java.lang.String value) {
      java.lang.Class<?> valueClass = value.getClass();
  
      method_ = value;
    }
    /**
     * <code>string method = 1;</code>
     */
    private void clearMethod() {
      
      method_ = getDefaultInstance().getMethod();
    }
    /**
     * <code>string method = 1;</code>
     * @param value The bytes for method to set.
     */
    private void setMethodBytes(
        com.google.protobuf.ByteString value) {
      checkByteStringIsUtf8(value);
      method_ = value.toStringUtf8();
      
    }

    public static final int REQUESTTIME_FIELD_NUMBER = 2;
    private java.lang.String requestTime_;
    /**
     * <code>string requestTime = 2;</code>
     * @return The requestTime.
     */
    @java.lang.Override
    public java.lang.String getRequestTime() {
      return requestTime_;
    }
    /**
     * <code>string requestTime = 2;</code>
     * @return The bytes for requestTime.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getRequestTimeBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(requestTime_);
    }
    /**
     * <code>string requestTime = 2;</code>
     * @param value The requestTime to set.
     */
    private void setRequestTime(
        java.lang.String value) {
      java.lang.Class<?> valueClass = value.getClass();
  
      requestTime_ = value;
    }
    /**
     * <code>string requestTime = 2;</code>
     */
    private void clearRequestTime() {
      
      requestTime_ = getDefaultInstance().getRequestTime();
    }
    /**
     * <code>string requestTime = 2;</code>
     * @param value The bytes for requestTime to set.
     */
    private void setRequestTimeBytes(
        com.google.protobuf.ByteString value) {
      checkByteStringIsUtf8(value);
      requestTime_ = value.toStringUtf8();
      
    }

    public static final int REQUESTID_FIELD_NUMBER = 3;
    private java.lang.String requestId_;
    /**
     * <code>string requestId = 3;</code>
     * @return The requestId.
     */
    @java.lang.Override
    public java.lang.String getRequestId() {
      return requestId_;
    }
    /**
     * <code>string requestId = 3;</code>
     * @return The bytes for requestId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getRequestIdBytes() {
      return com.google.protobuf.ByteString.copyFromUtf8(requestId_);
    }
    /**
     * <code>string requestId = 3;</code>
     * @param value The requestId to set.
     */
    private void setRequestId(
        java.lang.String value) {
      java.lang.Class<?> valueClass = value.getClass();
  
      requestId_ = value;
    }
    /**
     * <code>string requestId = 3;</code>
     */
    private void clearRequestId() {
      
      requestId_ = getDefaultInstance().getRequestId();
    }
    /**
     * <code>string requestId = 3;</code>
     * @param value The bytes for requestId to set.
     */
    private void setRequestIdBytes(
        com.google.protobuf.ByteString value) {
      checkByteStringIsUtf8(value);
      requestId_ = value.toStringUtf8();
      
    }

    public static com.apron.mobilesdk.state.ProtoRegister.Register parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static com.apron.mobilesdk.state.ProtoRegister.Register parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static com.apron.mobilesdk.state.ProtoRegister.Register parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static com.apron.mobilesdk.state.ProtoRegister.Register parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static com.apron.mobilesdk.state.ProtoRegister.Register parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data);
    }
    public static com.apron.mobilesdk.state.ProtoRegister.Register parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, data, extensionRegistry);
    }
    public static com.apron.mobilesdk.state.ProtoRegister.Register parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static com.apron.mobilesdk.state.ProtoRegister.Register parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static com.apron.mobilesdk.state.ProtoRegister.Register parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input);
    }
    public static com.apron.mobilesdk.state.ProtoRegister.Register parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return parseDelimitedFrom(DEFAULT_INSTANCE, input, extensionRegistry);
    }
    public static com.apron.mobilesdk.state.ProtoRegister.Register parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input);
    }
    public static com.apron.mobilesdk.state.ProtoRegister.Register parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageLite.parseFrom(
          DEFAULT_INSTANCE, input, extensionRegistry);
    }

    public static Builder newBuilder() {
      return (Builder) DEFAULT_INSTANCE.createBuilder();
    }
    public static Builder newBuilder(com.apron.mobilesdk.state.ProtoRegister.Register prototype) {
      return (Builder) DEFAULT_INSTANCE.createBuilder(prototype);
    }

    /**
     * Protobuf type {@code Register}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageLite.Builder<
          com.apron.mobilesdk.state.ProtoRegister.Register, Builder> implements
        // @@protoc_insertion_point(builder_implements:Register)
        com.apron.mobilesdk.state.ProtoRegister.RegisterOrBuilder {
      // Construct using com.apron.mobilesdk.state.ProtoRegister.Register.newBuilder()
      private Builder() {
        super(DEFAULT_INSTANCE);
      }


      /**
       * <code>string method = 1;</code>
       * @return The method.
       */
      @java.lang.Override
      public java.lang.String getMethod() {
        return instance.getMethod();
      }
      /**
       * <code>string method = 1;</code>
       * @return The bytes for method.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString
          getMethodBytes() {
        return instance.getMethodBytes();
      }
      /**
       * <code>string method = 1;</code>
       * @param value The method to set.
       * @return This builder for chaining.
       */
      public Builder setMethod(
          java.lang.String value) {
        copyOnWrite();
        instance.setMethod(value);
        return this;
      }
      /**
       * <code>string method = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearMethod() {
        copyOnWrite();
        instance.clearMethod();
        return this;
      }
      /**
       * <code>string method = 1;</code>
       * @param value The bytes for method to set.
       * @return This builder for chaining.
       */
      public Builder setMethodBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setMethodBytes(value);
        return this;
      }

      /**
       * <code>string requestTime = 2;</code>
       * @return The requestTime.
       */
      @java.lang.Override
      public java.lang.String getRequestTime() {
        return instance.getRequestTime();
      }
      /**
       * <code>string requestTime = 2;</code>
       * @return The bytes for requestTime.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString
          getRequestTimeBytes() {
        return instance.getRequestTimeBytes();
      }
      /**
       * <code>string requestTime = 2;</code>
       * @param value The requestTime to set.
       * @return This builder for chaining.
       */
      public Builder setRequestTime(
          java.lang.String value) {
        copyOnWrite();
        instance.setRequestTime(value);
        return this;
      }
      /**
       * <code>string requestTime = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearRequestTime() {
        copyOnWrite();
        instance.clearRequestTime();
        return this;
      }
      /**
       * <code>string requestTime = 2;</code>
       * @param value The bytes for requestTime to set.
       * @return This builder for chaining.
       */
      public Builder setRequestTimeBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setRequestTimeBytes(value);
        return this;
      }

      /**
       * <code>string requestId = 3;</code>
       * @return The requestId.
       */
      @java.lang.Override
      public java.lang.String getRequestId() {
        return instance.getRequestId();
      }
      /**
       * <code>string requestId = 3;</code>
       * @return The bytes for requestId.
       */
      @java.lang.Override
      public com.google.protobuf.ByteString
          getRequestIdBytes() {
        return instance.getRequestIdBytes();
      }
      /**
       * <code>string requestId = 3;</code>
       * @param value The requestId to set.
       * @return This builder for chaining.
       */
      public Builder setRequestId(
          java.lang.String value) {
        copyOnWrite();
        instance.setRequestId(value);
        return this;
      }
      /**
       * <code>string requestId = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearRequestId() {
        copyOnWrite();
        instance.clearRequestId();
        return this;
      }
      /**
       * <code>string requestId = 3;</code>
       * @param value The bytes for requestId to set.
       * @return This builder for chaining.
       */
      public Builder setRequestIdBytes(
          com.google.protobuf.ByteString value) {
        copyOnWrite();
        instance.setRequestIdBytes(value);
        return this;
      }

      // @@protoc_insertion_point(builder_scope:Register)
    }
    @java.lang.Override
    @java.lang.SuppressWarnings({"unchecked", "fallthrough"})
    protected final java.lang.Object dynamicMethod(
        com.google.protobuf.GeneratedMessageLite.MethodToInvoke method,
        java.lang.Object arg0, java.lang.Object arg1) {
      switch (method) {
        case NEW_MUTABLE_INSTANCE: {
          return new com.apron.mobilesdk.state.ProtoRegister.Register();
        }
        case NEW_BUILDER: {
          return new Builder();
        }
        case BUILD_MESSAGE_INFO: {
            java.lang.Object[] objects = new java.lang.Object[] {
              "method_",
              "requestTime_",
              "requestId_",
            };
            java.lang.String info =
                "\u0000\u0003\u0000\u0000\u0001\u0003\u0003\u0000\u0000\u0000\u0001\u0208\u0002\u0208" +
                "\u0003\u0208";
            return newMessageInfo(DEFAULT_INSTANCE, info, objects);
        }
        // fall through
        case GET_DEFAULT_INSTANCE: {
          return DEFAULT_INSTANCE;
        }
        case GET_PARSER: {
          com.google.protobuf.Parser<com.apron.mobilesdk.state.ProtoRegister.Register> parser = PARSER;
          if (parser == null) {
            synchronized (com.apron.mobilesdk.state.ProtoRegister.Register.class) {
              parser = PARSER;
              if (parser == null) {
                parser =
                    new DefaultInstanceBasedParser<com.apron.mobilesdk.state.ProtoRegister.Register>(
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


    // @@protoc_insertion_point(class_scope:Register)
    private static final com.apron.mobilesdk.state.ProtoRegister.Register DEFAULT_INSTANCE;
    static {
      Register defaultInstance = new Register();
      // New instances are implicitly immutable so no need to make
      // immutable.
      DEFAULT_INSTANCE = defaultInstance;
      com.google.protobuf.GeneratedMessageLite.registerDefaultInstance(
        Register.class, defaultInstance);
    }

    public static com.apron.mobilesdk.state.ProtoRegister.Register getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static volatile com.google.protobuf.Parser<Register> PARSER;

    public static com.google.protobuf.Parser<Register> parser() {
      return DEFAULT_INSTANCE.getParserForType();
    }
  }


  static {
  }

  // @@protoc_insertion_point(outer_class_scope)
}
