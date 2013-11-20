/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/abhishek/workspace/NDNDroid/aidl/com/example/ndndroid/NDNBackgroundServiceApi.aidl
 */
package com.example.ndndroid;
public interface NDNBackgroundServiceApi extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.ndndroid.NDNBackgroundServiceApi
{
private static final java.lang.String DESCRIPTOR = "com.example.ndndroid.NDNBackgroundServiceApi";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.ndndroid.NDNBackgroundServiceApi interface,
 * generating a proxy if needed.
 */
public static com.example.ndndroid.NDNBackgroundServiceApi asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.ndndroid.NDNBackgroundServiceApi))) {
return ((com.example.ndndroid.NDNBackgroundServiceApi)iin);
}
return new com.example.ndndroid.NDNBackgroundServiceApi.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_startNDNBackgroundService:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _result = this.startNDNBackgroundService();
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_addNewConnection:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _result = this.addNewConnection(_arg0, _arg1);
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.ndndroid.NDNBackgroundServiceApi
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public java.lang.String startNDNBackgroundService() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_startNDNBackgroundService, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public java.lang.String addNewConnection(java.lang.String mac, java.lang.String prefix) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(mac);
_data.writeString(prefix);
mRemote.transact(Stub.TRANSACTION_addNewConnection, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_startNDNBackgroundService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_addNewConnection = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public java.lang.String startNDNBackgroundService() throws android.os.RemoteException;
public java.lang.String addNewConnection(java.lang.String mac, java.lang.String prefix) throws android.os.RemoteException;
}