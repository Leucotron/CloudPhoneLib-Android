# CloudPhoneLib-Android
Cloud Phone Library for Android - Powered by Leucotron

Descrição
-----------

Essa biblioteca foi criada para facilitar a implementação do serviço Cloud Phone Leucotron em aplicações Android existentes.

A minSdkVersion deve ser 21 para seu perfeito funcionamento.

Não foram utilizadas as bibliotecas AndroidX para compatibilizar a biblioteca com aplicações em geral.

**Como usar:**

1)Adicione ao final dos repositórios a URL do jitpack. Essa instrução está no seu build.gradle da pasta raiz do projeto: 

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2) Adiciona a dependência:

```
dependencies {
	        implementation 'com.github.Leucotron:CloudPhoneLib-Android:0.1.1'
	}
```
