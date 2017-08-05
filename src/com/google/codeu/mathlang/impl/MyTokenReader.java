// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.codeu.mathlang.impl;

import java.io.IOException;
import com.google.codeu.mathlang.core.tokens.*;
import com.google.codeu.mathlang.parsing.TokenReader;

// MY TOKEN READER
//
// This is YOUR implementation of the token reader interface. To know how
// it should work, read src/com/google/codeu/mathlang/parsing/TokenReader.java.
// You should not need to change any other files to get your token reader to
// work with the test of the system.
public final class MyTokenReader implements TokenReader {

  private String source;
  private StringBuilder token;
  private int index;

  public MyTokenReader(String source) {
    // Your token reader will only be given a string for input. The string will
    // contain the whole source (0 or more lines).

    this.source = source;
    this.token = new StringBuilder();
    this.index = 0;
  }

  @Override
  public Token next() throws IOException {
    // Most of your work will take place here. For every call to |next| you should
    // return a token until you reach the end. When there are no more tokens, you
    // should return |null| to signal the end of input.

    // If for any reason you detect an error in the input, you may throw an IOException
    // which will stop all execution.

    token.setLength(0);

    try {
      skip(' ');

      if(isEnd())
        return null;

      if(lookAhead() == '"'){
        read();
        token.append(readUntil('"'));
        read();

        return new StringToken(token.toString());
      }
      else if(lookAhead() == ';'){
        token.append(read());

        if(!isEnd() && lookAhead() == '\n')
          read();
      }
      else {
        token.append(readUntil(' '));
      }
    } catch(Exception e){
        throw new IOException(e.getMessage());
    }

    //System.out.println("--------TOKEN: " + token.toString());

    return determineToken(token.toString().trim());
  }

  private void skip(char skipMarker) throws IOException{
    while(!isEnd() && lookAhead() == skipMarker)
      read();
  }

  private String readUntil(char marker) throws IOException{
    StringBuilder builder = new StringBuilder();
    builder.setLength(0);

    while(!isEnd() && (lookAhead() != marker && lookAhead() != ';')) {
      builder.append(read());
    }

    return builder.toString();
  }

  private char read() throws IOException{
    if(isEnd())
      throw new IOException("No more characters to read.");
    return source.charAt(index++);
  }

  private boolean isEnd(){
    return (source.length() - index) == 0;
  }

  private char lookAhead() throws IOException{
    if(isEnd())
      throw new IOException("End of string!");
    return source.charAt(index);
  }

  private Token determineToken(String s) throws IOException{
    if(isNumber(s))
      return new NumberToken(Double.parseDouble(s));
    if(isName(s))
      return new NameToken(s);
    if(isSymbol(s))
      return new SymbolToken(s.charAt(0));
    return new StringToken(s);
  }

  private boolean isNumber(String s) throws IOException{
    int start = 0;
    if(s.length() > 1 && s.charAt(0) == '-' && Character.isDigit(s.charAt(1)))
      start = 1;

    for(int i = start; i < s.length(); i++)
      if(!Character.isDigit(s.charAt(i)))
        return false;

    return true;
  }

  private boolean isName(String s){
    for(char c : s.toCharArray())
      if(Character.isWhitespace(c))
        return false;

    return Character.isAlphabetic(s.charAt(0));
  }

  private boolean isSymbol(String s){
    return s.length() == 1 && !Character.isLetterOrDigit(s.charAt(0));
  }

}
