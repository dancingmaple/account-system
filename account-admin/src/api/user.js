import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/v1/users',
    method: 'get',
    params
  })
}

export function getUserDetail(id) {
  return request({
    url: `/v1/users/${id}`,
    method: 'get'
  })
}

export function createUser(data) {
  return request({
    url: '/v1/users',
    method: 'post',
    data
  })
}

export function updateUser(id, data) {
  return request({
    url: `/v1/users/${id}`,
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/v1/users/${id}`,
    method: 'delete'
  })
}

export function assignRoles(id, data) {
  return request({
    url: `/v1/users/${id}/roles`,
    method: 'put',
    data
  })
}

export function resetPassword(id, newPassword) {
  return request({
    url: `/v1/users/${id}/reset-password`,
    method: 'post',
    params: { newPassword }
  })
}

export function changePassword(oldPassword, newPassword) {
  return request({
    url: '/v1/users/change-password',
    method: 'post',
    params: { oldPassword, newPassword }
  })
}