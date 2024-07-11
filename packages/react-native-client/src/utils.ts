export function isJest() {
  // @ts-expect-error this function should be removed in FCE-270
  return process.env.NODE_ENV === 'test';
}
