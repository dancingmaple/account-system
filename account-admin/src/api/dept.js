import request from '@/utils/request'

export function getDeptList(params) {
  return request({
    url: '/v1/depts/tree',
    method: 'get',
    params
  })
}

export function getDeptTree() {
  return request({
    url: '/v1/depts/tree',
    method: 'get'
  })
}

export function getDeptDetail(id) {
  return request({
    url: `/v1/depts/${id}`,
    method: 'get'
  })
}

export function createDept(data) {
  return request({
    url: '/v1/depts',
    method: 'post',
    data
  })
}

export function updateDept(id, data) {
  return request({
    url: `/v1/depts/${id}`,
    method: 'put',
    data
  })
}

export function deleteDept(id) {
  return request({
    url: `/v1/depts/${id}`,
    method: 'delete'
  })
}