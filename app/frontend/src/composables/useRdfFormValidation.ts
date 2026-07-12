import { computed, type Ref } from 'vue'
import { getRdfValidationError, getRdfValidationNote } from '../utils/util.ts'

interface FormFields {
  subject: Ref<string>
  subjectUri: Ref<string>
  predicate: Ref<string>
  predicateUri: Ref<string>
  object: Ref<string>
  objectUri: Ref<string>
  objectMode: Ref<'uri' | 'literal'>
}

export function useRdfFormValidation(fields: FormFields) {
  const validationError = computed(() => {
    return getRdfValidationError({
      subjectUri: fields.subjectUri.value,
      predicateUri: fields.predicateUri.value,
      objectUri: fields.objectUri.value,
      objectMode: fields.objectMode.value,
    })
  })

  const validationNote = computed(() => {
    return getRdfValidationNote({
      subject: fields.subject.value,
      predicate: fields.predicate.value,
      object: fields.object.value,
    })
  })

  return {
    validationError,
    validationNote,
  }
}
