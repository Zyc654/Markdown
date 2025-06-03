/***************************************************************************
 * 
 * Copyright (c) 2022 str2num.com, Inc. All Rights Reserved
 * $Id$ 
 * 
 **************************************************************************/
 
 
 
/**
 * @file tcp_connection.cpp
 * @author str2num
 * @version $Revision$ 
 * @brief 
 *  
 **/

#include "server/tcp_connection.h"

namespace xrtc {

TcpConnection::TcpConnection(int fd) : 
    fd(fd),
    querybuf(sdsempty())
{
}

TcpConnection::~TcpConnection() {
    sdsfree(querybuf);
}

} // namespace xrtc



















