# stats-intellij-plugin
Simple plugin for Intellij IDEA which counts Kotlin file statistics

## Как это работает
### Текущая реализация
Алгоритм плагина использует довольно наивное представление о структуре проекта:
* Source-файлы – файлы, лежащие в папках src и main
* Test Source-файлы – файлы, лежаещие в папке test

В том числе, один из минусов текущего подхода – считаем, что любые файлы, лежащие в этих папках – source-файлы. 
Потенциально от этого можно избавится перечислением всевозможных расширений файлов, которые считаются source-файлами.

Алгоритм обходит дерево текущего проекта и собирает статистику по файлам с помощью `StatsCollectingVisitor`. 
Если находим каталог с source-файлами, то помечаем всё поддерево меткой `SRC`. Если находим каталог с test-файлами, то 
помечаем всё поддерево меткой `TEST`. Остальный файлы помечаются меткой `OTHER`. Статистика считается только для файлов,
помеченных меткой `TEST` или `SRC`.

### Иной подход
Другой возможный подход – для каждого файла при обходе дерева проекта смотрет, какому корневому модулю он принадлежит – main 
или test.

### Идеальный вариант
На мой взгляд, самым грамотным вариантом был бы поиск меток модулей
(https://www.jetbrains.com/help/idea/working-with-modules.html). Однако, в текущий документации я не 
нашел ничего связанного с этим.

**UPD (после дедлайна)**: нашел, что можно использовать `ModuleRootManager.getInstance(module).getFileIndex().isInTestSourceContent(file)` для этих целей. Версия с использованием этого метода в ветке `after_deadline` (https://github.com/pestretsov/stats-intellij-plugin/tree/after_deadline)

## Пример работы плагина

<img width="1440" alt="screen shot 2018-02-18 at 10 50 39 pm" src="https://user-images.githubusercontent.com/3885667/36356212-9846b6d0-14ff-11e8-8be5-b4dd79027d39.png">

<img width="1440" alt="screen shot 2018-02-18 at 10 57 37 pm" src="https://user-images.githubusercontent.com/3885667/36356214-9d797174-14ff-11e8-9fa2-1278ea0dc26d.png">
