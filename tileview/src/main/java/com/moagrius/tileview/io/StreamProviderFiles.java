package com.moagrius.tileview.io;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Locale;

public class StreamProviderFiles implements StreamProvider {

  @Override
  public InputStream getStream(int column, int row, Context context, Object data) throws Exception {
    String path = String.format(Locale.US, (String) data, column, row);
    Log.d("TV", "provider, path=" + path);
    File file = new File(path);
    Log.d("TV", "provider, file=" + file);
    Log.d("TV", "provider, file.length=" + file.length());
    try {
      InputStream stream = new FileInputStream(file);
      stream = new BufferedInputStream(stream);
      Log.d("TV", "after stream....");
      Log.d("TV", "steam available=" + stream.available());
      return stream;
    } catch (Exception e) {
      Log.d("TV", "SD card stream exception: " + e.getMessage());
      e.printStackTrace();
      return null;
    }
  }

}
