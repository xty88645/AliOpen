/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.alibaba.tuna.netty.handler.codec.http;

import com.alibaba.tuna.netty.buffer.ByteBuf;
import com.alibaba.tuna.netty.util.CharsetUtil;

import static com.alibaba.tuna.netty.handler.codec.http.HttpConstants.*;

/**
 * Encodes an {@link HttpRequest} or an {@link HttpContent} into
 * a {@link ByteBuf}.
 */
public class HttpRequestEncoder extends HttpObjectEncoder<HttpRequest> {
    private static final char SLASH = '/';
    private static final byte[] CRLF = { CR, LF };

    @Override
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return super.acceptOutboundMessage(msg) && !(msg instanceof HttpResponse);
    }

    @Override
    protected void encodeInitialLine(ByteBuf buf, HttpRequest request) throws Exception {
        request.getMethod().encode(buf);
        buf.writeByte(SP);

        // Add / as absolute path if no is present.
        // See http://tools.ietf.org/html/rfc2616#section-5.1.2
        String uri = request.getUri();

        if (uri.length() == 0) {
            uri += SLASH;
        } else {
            int start = uri.indexOf("://");
            if (start != -1 && uri.charAt(0) != SLASH) {
                int startIndex = start + 3;
                if (uri.lastIndexOf(SLASH) <= startIndex) {
                    uri += SLASH;
                }
            }
        }

        buf.writeBytes(uri.getBytes(CharsetUtil.UTF_8));

        buf.writeByte(SP);
        request.getProtocolVersion().encode(buf);
        buf.writeBytes(CRLF);
    }
}
