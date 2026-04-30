import request from '@/utils/request'

export function getRoleList(params) {
  return request({
    url: '/v1/roles',
    method: 'get',
    params
  })
}

export function getRoleDetail(id) {
  return request({
    url: `/v1/roles/${id}`,
    method: 'get'
  })
}

export function createRole(data) {
  return request({
    url: '/v1/roles',
    method: 'post',
    data
  })
}

export function updateRole(id, data) {
  return request({
    url: `/v1/roles/${id}`,
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/v1/roles/${id}`,
    method: 'delete'
  })
}

export function assignMenus(id, data) {
  return request({
    url: `/v1/roles/${id}/menus`,
    method: 'put',
    data
  })
}

export function assignDataScope(id, dataScope, deptIds) {
  return request({
    url: `/v1/roles/${id}/data-scope`,
    method: 'put',
    params: { dataScope },
    data: deptIds
  })
}